package olhotak.liveness;
import soot.*;
import java.io.*;

public class LiveVariablesMain {
    public static void main(String[] args) 
    {
	if(args.length == 0)
	{
	    System.out.println("Syntax: java "+
		"olhotak.liveness.LiveVariablesMain mainClass "+
		"[soot options]");
	    System.exit(0);
	}            

	PackManager.v().getPack("jtp").add(
	    new Transform("jtp.liveVariables",
                new LiveVariablesTagger() ) );

	soot.Main.main(args);
    }
}


