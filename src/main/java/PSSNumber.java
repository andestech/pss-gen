import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.math.*;

public class PSSNumber {
	public static PSSVal newBinNumber(String text) {
		Pattern pattern = Pattern.compile("0[bB]([01_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String number_s = m.group(1).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Unsized unbased hexadecimal and binary numbers shall be treated as unsigned.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = false;
			int width = PSSIntModel.DEFAULT_INT_SIZE;
			BigInteger val = new BigInteger(number_s, 2);
			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not binary number");
		return null;
	}
	public static PSSVal newOctNumber(String text) {
		Pattern pattern = Pattern.compile("0([0-7_]*)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String number_s = m.group(1).replaceAll("_", "");
			if (number_s.equals("")) number_s = "0";

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Simple decimal and octal numbers without the size and the base format shall be treated as signed integers.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = true;
			int width = PSSIntModel.DEFAULT_INT_SIZE;
			BigInteger val = new BigInteger(number_s, 8);
			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not otcal number");
		return null;
	}
	public static PSSVal newDecNumber(String text) {
		Pattern pattern = Pattern.compile("([1-9][0-9_]*)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String number_s = m.group(1).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Simple decimal and octal numbers without the size and the base format shall be treated as signed integers.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = true;
			int width = PSSIntModel.DEFAULT_INT_SIZE;
			BigInteger val = new BigInteger(number_s, 10);
			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not decimal number");
		return null;
	}
	public static PSSVal newHexNumber(String text) {
		Pattern pattern = Pattern.compile("0[xX]([0-9a-fA-F_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String number_s = m.group(1).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Unsized unbased hexadecimal and binary numbers shall be treated as unsigned.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = false;
			int width = PSSIntModel.DEFAULT_INT_SIZE;
			BigInteger val = new BigInteger(number_s, 16);
			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not hexadecimal number");
		return null;
	}
	public static PSSVal newBasedBinNumber(String text) {
		Pattern pattern = Pattern.compile("([0-9]*)'([sS]?)[bB]([01_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String width_s  = m.group(1)                    ;
			String sign_s   = m.group(2)                    ;
			String number_s = m.group(3).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Unsized unbased hexadecimal and binary numbers shall be treated as unsigned.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = (sign_s.equals("")) ? false : true;
			int width = (width_s.equals("")) ? PSSIntModel.DEFAULT_INT_SIZE : Integer.parseUnsignedInt(width_s);
			BigInteger val = new BigInteger(number_s, 2);

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * If the size of an unsigned number is larger than the size specified for the literal constant,
			 * the unsigned number shall be truncated from the left.
			 */
			if (width < val.bitCount()) {
				BigInteger mask = BigInteger.ONE.shiftLeft(width + 1);
				mask = mask.subtract(BigInteger.ONE);
				val = val.and(mask);
			}

			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not base based binary number");
		return null;
	}
	public static PSSVal newBasedOctNumber(String text) {
		// 64'o1
		//   'o2
		Pattern pattern = Pattern.compile("([0-9]*)'([sS]?)[oO]([0-7_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String width_s  = m.group(1)                    ;
			String sign_s   = m.group(2)                    ;
			String number_s = m.group(3).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Simple decimal and octal numbers without the size and the base format shall be treated as signed integers.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = (sign_s.equals("")) ? (width_s.equals("")) : true;
			int width = (width_s.equals("")) ? PSSIntModel.DEFAULT_INT_SIZE : Integer.parseUnsignedInt(width_s);
			BigInteger val = new BigInteger(number_s, 8);

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * If the size of an unsigned number is larger than the size specified for the literal constant,
			 * the unsigned number shall be truncated from the left.
			 */
			if (width < val.bitCount()) {
				BigInteger mask = BigInteger.ONE.shiftLeft(width + 1);
				mask = mask.subtract(BigInteger.ONE);
				val = val.and(mask);
			}

			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not base based octal number");
		return null;
	}
	public static PSSVal newBasedDecNumber(String text) {
		// 64'd1
		//   'd2
		Pattern pattern = Pattern.compile("([0-9]*)'([sS]?)[dD]([0-9_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String width_s  = m.group(1)                    ;
			String sign_s   = m.group(2)                    ;
			String number_s = m.group(3).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Simple decimal and octal numbers without the size and the base format shall be treated as signed integers.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = (sign_s.equals("")) ? (width_s.equals("")) : true;
			int width = (width_s.equals("")) ? PSSIntModel.DEFAULT_INT_SIZE : Integer.parseUnsignedInt(width_s);
			BigInteger val = new BigInteger(number_s, 10);

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * If the size of an unsigned number is larger than the size specified for the literal constant,
			 * the unsigned number shall be truncated from the left.
			 */
			if (width < val.bitCount()) {
				BigInteger mask = BigInteger.ONE.shiftLeft(width + 1);
				mask = mask.subtract(BigInteger.ONE);
				val = val.and(mask);
			}

			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not base based decimal number");
		return null;
	}
	public static PSSVal newBasedHexNumber(String text) {
		// 64'h00000000E0500000
		//   'h00000000E0500000
		Pattern pattern = Pattern.compile("([0-9]*)'([sS]?)[hH]([0-9a-fA-F_]+)");
		Matcher m = pattern.matcher(text);
		if (m.find()) {
			String width_s  = m.group(1)                    ;
			String sign_s   = m.group(2)                    ;
			String number_s = m.group(3).replaceAll("_", "");

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * Unsized unbased hexadecimal and binary numbers shall be treated as unsigned.
			 * Numbers specified with a base format shall be treated as signed integers only if the s designator is included.
			 * If the s designator is no included, the number shall be treated as an unsigned integer.
			 */
			boolean sign = (sign_s.equals("")) ? false : true;
			int width = (width_s.equals("")) ? PSSIntModel.DEFAULT_INT_SIZE : Integer.parseUnsignedInt(width_s);
			BigInteger val = new BigInteger(number_s, 16);

			/**
			 * LRM 3.0 Section 4.6.1 Integer constants
			 * If the size of an unsigned number is larger than the size specified for the literal constant,
			 * the unsigned number shall be truncated from the left.
			 */
			if (width < val.bitCount()) {
				BigInteger mask = BigInteger.ONE.shiftLeft(width + 1);
				mask = mask.subtract(BigInteger.ONE);
				val = val.and(mask);
			}

			return new PSSIntVal(val, width, sign);
		}
		PSSMessage.Fatal("'" + text + "' is not base based hexadecimal number");
		return null;
	}
}
