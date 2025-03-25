package pieker.dsl;

import picocli.CommandLine;

public class ArgumentHandler {


    @CommandLine.Option(names = "--dir", description = "The dir option sets the directory for additional resources referenced inside PIEKER DSL.")
    protected String resourceDirectory;

    @CommandLine.Option(names = "--dsl", description = "The dsl option sets the path to a PIEKER DSL file.")
    protected String inputFile;

}
