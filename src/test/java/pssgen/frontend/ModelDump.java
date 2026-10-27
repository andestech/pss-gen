package pssgen.frontend;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * A text form of the object graph that the model build leaves behind: every object
 * reachable from the root through instance fields, as a tree in field order. An
 * object met again prints as {@code @<n>}, the number of its first appearance, so
 * shared nodes and back pointers are part of the text; objects without state (no
 * fields, lambdas, empty collections) print without a number, and a field that
 * is null is left out. Field names, class simple
 * names, scalar values and the order of collections are recorded; nothing that
 * depends on identity hashes or on where a class lives.
 */
final class ModelDump {

	/** fields that hold the run's services, not the model */
	private static final List<String> SKIPPED_TYPES = Arrays.asList("pssgen.util.PSSLog", "pssgen.util.PSSRun",
		"java.io.PrintStream", "java.util.Random");

	private final IdentityHashMap<Object, Integer> m_ids = new IdentityHashMap<Object, Integer>();
	private final StringBuilder m_out = new StringBuilder();

	private ModelDump() {
	}

	static String of(Object root) {
		ModelDump d = new ModelDump();
		d.value(root, "");
		return d.m_out.toString();
	}

	/** Appends o after a label already written on the current line. */
	private void value(Object o, String indent) {
		if (o == null) {
			m_out.append("null\n");
		} else if (o instanceof String) {
			m_out.append(quote((String) o)).append('\n');
		} else if (o instanceof Boolean || o instanceof Character || o instanceof Integer || o instanceof Long
			|| o instanceof Short || o instanceof Byte || o instanceof BigInteger || o instanceof BigDecimal) {
			m_out.append(o).append('\n');
		} else if (o instanceof Enum) {
			m_out.append(((Enum<?>) o).getDeclaringClass().getSimpleName()).append('.').append(((Enum<?>) o).name())
				.append('\n');
		} else if (o.getClass().isSynthetic() || o.getClass().getName().contains("$$Lambda")) {
			m_out.append("<lambda>\n");
		} else if (o.getClass().getName().startsWith("pssgen.") && fields(o.getClass()).isEmpty()) {
			m_out.append(o.getClass().getSimpleName()).append(" {}\n");
		} else if (m_ids.containsKey(o)) {
			m_out.append('@').append(m_ids.get(o)).append('\n');
		} else {
			int id = m_ids.size() + 1;
			m_ids.put(o, id);
			String inner = indent + "  ";
			if (o instanceof Collection && ((Collection<?>) o).isEmpty()) {
				m_ids.remove(o);
				m_out.append(o.getClass().getSimpleName()).append("[0]\n");
			} else if (o instanceof Collection) {
				Collection<?> c = (Collection<?>) o;
				m_out.append('#').append(id).append(' ').append(o.getClass().getSimpleName()).append('[')
					.append(c.size()).append("]\n");
				for (Object e : c) {
					m_out.append(inner).append("- ");
					value(e, inner);
				}
			} else if (o instanceof Map) {
				Map<?, ?> m = (Map<?, ?>) o;
				m_out.append('#').append(id).append(' ').append(o.getClass().getSimpleName()).append('[')
					.append(m.size()).append("]\n");
				for (Map.Entry<?, ?> e : m.entrySet()) {
					m_out.append(inner).append("key: ");
					value(e.getKey(), inner);
					m_out.append(inner).append("val: ");
					value(e.getValue(), inner);
				}
			} else if (o.getClass().isArray()) {
				int n = Array.getLength(o);
				m_out.append('#').append(id).append(' ').append(o.getClass().getComponentType().getSimpleName())
					.append("[] [").append(n).append("]\n");
				for (int i = 0; i < n; i++) {
					m_out.append(inner).append("- ");
					value(Array.get(o, i), inner);
				}
			} else if (!o.getClass().getName().startsWith("pssgen.")) {
				m_out.append('#').append(id).append(" <").append(o.getClass().getName()).append(">\n");
			} else {
				m_out.append('#').append(id).append(' ').append(o.getClass().getSimpleName()).append('\n');
				for (Field f : fields(o.getClass())) {
					Object v;
					try {
						v = f.get(o);
					} catch (IllegalAccessException e) {
						throw new AssertionError(e);
					}
					if (v == null)
						continue;
					m_out.append(inner).append(f.getName()).append(": ");
					value(v, inner);
				}
			}
		}
	}

	/** The instance fields of c, superclass fields first, each class's fields by name. */
	private static List<Field> fields(Class<?> c) {
		List<Field> res = new ArrayList<Field>();
		if (c.getSuperclass() != null && c.getSuperclass().getName().startsWith("pssgen."))
			res.addAll(fields(c.getSuperclass()));
		List<Field> own = new ArrayList<Field>();
		for (Field f : c.getDeclaredFields()) {
			if (Modifier.isStatic(f.getModifiers()) || f.isSynthetic() || SKIPPED_TYPES.contains(f.getType().getName()))
				continue;
			f.setAccessible(true);
			own.add(f);
		}
		own.sort(Comparator.comparing(Field::getName));
		res.addAll(own);
		return res;
	}

	private static String quote(String s) {
		StringBuilder b = new StringBuilder("\"");
		for (char ch : s.toCharArray()) {
			if (ch == '"' || ch == '\\')
				b.append('\\').append(ch);
			else if (ch == '\n')
				b.append("\\n");
			else if (ch == '\r')
				b.append("\\r");
			else if (ch == '\t')
				b.append("\\t");
			else if (ch < 0x20 || ch > 0x7e)
				b.append(String.format("\\u%04x", (int) ch));
			else
				b.append(ch);
		}
		return b.append('"').toString();
	}
}
