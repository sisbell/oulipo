/*******************************************************************************
 * OulipoMachine licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License.  
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

	public static void main(String[] args) {
		  CommandLineParser parser = new PosixParser();
	        CommandLine commandLine = null;

	        try {
	            commandLine = parser.parse(addOptions(), args);
	        } catch (ParseException ex) {
	            exit("Invalid command arguments");
	        }
	        
	        
	}
	
    private static void exit(String message) {
        System.out.println("\r\n" + message + "\r\n");
      //  printHelp();
        System.exit(-1);
    }
    
	private static Options addOptions() {
        Options options = new Options();
        Option helpOption = Option.builder("h").longOpt("help")
                .desc("Display help").build();
        Option databaseOption = Option.builder("db").longOpt("databaseDir")
                .desc("Directory of database").build();
        Option portOption = Option.builder("p").longOpt("port")
                .desc("Server port").build();
        
        Option summaryOption = OptionBuilder.withLongOpt("summary")
                .withDescription("Output as summary statics")
                .create("s");
        Option outputOption = OptionBuilder.withLongOpt("output")
                .withDescription("Log output file")
                .create("o");
        Option rawOption = OptionBuilder.withLongOpt("raw")
                .withDescription("Output as raw delimited format")
                .create("r");

        
        Option configOption = OptionBuilder.hasOptionalArg().withLongOpt("config")
                .withDescription("config.json file for defining log events")
                .create("c");

        return options.addOption(portOption).addOption(databaseOption).addOption(helpOption).addOption(rawOption)
                .addOption(outputOption).addOption(configOption);
    }
}
