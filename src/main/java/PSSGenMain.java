/**
 * Starts PSSGen under the class name that existing command lines use
 * ("java -classpath ... PSSGenMain [options] file..."). The program itself
 * is pssgen.cli.PSSGenMain.
 */
public class PSSGenMain {
	public static void main(String[] args) throws Exception {
		pssgen.cli.PSSGenMain.main(args);
	}
}
