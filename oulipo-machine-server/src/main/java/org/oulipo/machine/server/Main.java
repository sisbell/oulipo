/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License");  you may not use this file except in compliance with the License.  
 *
 * You may obtain a copy of the License at
 *   
 *       http://www.apache.org/licenses/LICENSE-2.0
 *    
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See the NOTICE file distributed with this work for 
 * additional information regarding copyright ownership. 
 *******************************************************************************/
package org.oulipo.machine.server;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class Main {

	private static Options addOptions() {
        Options options = new Options();
        Option helpOption = Option.builder("h").longOpt("help")
                .desc("Display help").build();
        Option databaseOption = Option.builder("db").longOpt("databaseDir")
                .desc("Directory of database").build();
        Option portOption = Option.builder("p").longOpt("port")
                .desc("Server port").build();
        
        OptionBuilder.withLongOpt("summary");
		OptionBuilder
                .withDescription("Output as summary statics");
		Option summaryOption = OptionBuilder
                .create("s");
        OptionBuilder.withLongOpt("output");
		OptionBuilder
                .withDescription("Log output file");
		Option outputOption = OptionBuilder
                .create("o");
        OptionBuilder.withLongOpt("raw");
		OptionBuilder
                .withDescription("Output as raw delimited format");
		Option rawOption = OptionBuilder
                .create("r");

        
        OptionBuilder.hasOptionalArg();
		OptionBuilder.withLongOpt("config");
		OptionBuilder
                .withDescription("config.json file for defining log events");
		Option configOption = OptionBuilder
                .create("c");

        return options.addOption(portOption).addOption(databaseOption).addOption(helpOption).addOption(rawOption)
                .addOption(outputOption).addOption(configOption);
    }
	
    private static void exit(String message) {
        System.out.println("\r\n" + message + "\r\n");
      //  printHelp();
        System.exit(-1);
    }
    
	public static void main(String[] args) {
		  CommandLineParser parser = new PosixParser();
	        CommandLine commandLine = null;

	        try {
	            commandLine = parser.parse(addOptions(), args);
	        } catch (ParseException ex) {
	            exit("Invalid command arguments");
	        }
	        
	        
	}
}
