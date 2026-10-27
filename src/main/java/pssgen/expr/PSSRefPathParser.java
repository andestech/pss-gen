package pssgen.expr;

import pssgen.model.PSSModel;
import pssgen.util.PSSLog;

/**
 * Turns the text of a reference into a reference expression. The frontend
 * provides it: the core packages do not depend on the parser.
 */
public interface PSSRefPathParser {

	/**
	 * Parses a reference from a string.
	 *
	 * @param root the model where the string is evaluated
	 * @param str  a string to be parsed
	 * @param log  the run's messages
	 * @return the reference denoted by the string
	 */
	PSSRefPathExpression fromString(PSSModel root, String str, PSSLog log);
}
