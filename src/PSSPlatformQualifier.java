
/**
 * A {@code PSSPlatformQualifier} can be either {@code target} or {@code solve}.
 * According to PSS 2.0 Section 22.4.1.3, "Functions restricted to the solve
 * platform shall not be called directly or indirectly from target execs, namely
 * body, run_start, and run_end. Similarly, functions restricted to the target
 * platform shall not be called from solve execs, namely init_down, init_up,
 * pre_solve, and post_solve."
 */
public enum PSSPlatformQualifier {

    /** the target platform */
    target,

    /** the solve platform */
    solve;

    /**
     * Returns {@code true} if a specified exec kind is available in this platform.
     *
     * @param k an exec kind
     * @return {@code true} if {@code k} is available in this platform
     */
    public boolean isAvailable(PSSExecKind k) {
        if (this == target) {
            return !PSSExecKind.body.equals(k) && !PSSExecKind.run_start.equals(k) && !PSSExecKind.run_end.equals(k);
        } else if (this == solve) {
            return !PSSExecKind.init.equals(k) && !PSSExecKind.init_up.equals(k) && !PSSExecKind.init_down.equals(k)
                    && !PSSExecKind.pre_solve.equals(k) && !PSSExecKind.post_solve.equals(k);
        } else {
            throw new IllegalArgumentException("Unimplemented platform qualifier " + this);
        }
    }

    /**
     * Returns {@code true} if the exec kind of a specified instance is
     * available in this platform.
     *
     * @param inst an instance
     * @return {@code true} if the exec kind of {@code inst} is available in this
     *         platform
     */
    public boolean isAvailable(PSSInst inst) {
        while (inst != null) {
            PSSModel m = inst.getTypeModel();
            if (m instanceof PSSExecBlock) {
                if (!isAvailable(((PSSExecBlock) m).getKind()))
                    return false;
            }
            if (inst instanceof PSSFunctionInst) {
                if (!isAvailable(((PSSFunctionInst) inst).getExecKind()))
                    return false;
            }

            inst = inst.m_parent;
        }
        return true;
    }

}