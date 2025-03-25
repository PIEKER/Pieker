package pieker.app;

import picocli.CommandLine;

public class ArgumentHandler {

    @CommandLine.Option(names = "--dsl-resources", description = "The dir option sets the directory for additional resources referenced inside PIEKER DSL.")
    protected String dslResourceDirectory = ".";

    @CommandLine.Option(names = "--dsl", description = "The dsl option sets the path to a PIEKER DSL file.", required = true)
    protected String dslFilePath;

    @CommandLine.Option(names = "--validate", description = "indicates validation-only on provided DSL file.")
    protected boolean validateOnly;

    @CommandLine.Option(names = "--dsl-config-only", description = "prevents any code generation. Only a configuration json will generated.")
    protected boolean dslConfigOnly;

}
