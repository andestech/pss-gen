antlr-4.9.3-complete.jar is not stored in git. `make` in `src/` downloads
`org.antlr:antlr4:4.9.3:complete` from Maven Central and installs it in this
directory under this name (task `installAntlrJar` in `../build.gradle`), where
the Makefiles and the documented command lines expect it.

See
	https://www.antlr.org/download.html
	https://github.com/antlr/website-antlr4/tree/gh-pages/download

