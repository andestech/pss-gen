
/**
 * This is an enumeration of exec_kind. See PSS 2.0 Section 22.1.3 for more
 * details.
 */
public enum PSSExecKind {

    /** pre_solve */
    pre_solve,

    /** post_solve */
    post_solve,

    /** body */
    body,

    /** header */
    header,

    /** declaration */
    declaration,

    /** run_start */
    run_start,

    /** run_end */
    run_end,

    /** init_down */
    init_down,

    /** init_up */
    init_up,

    /** an alias of init_up */
    init

}