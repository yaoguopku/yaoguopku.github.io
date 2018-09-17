package olhotak.liveness;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;

/** A body transformer that records live variable 
 * information in tags. */
public class LiveVariablesTagger extends BodyTransformer
{ 
    protected void internalTransform(
            Body b, String phaseName, Map options)
    {
        LiveVariablesAnalysis a = new LiveVariablesAnalysis(
		new BriefUnitGraph( b ) );

        Iterator sIt = b.getUnits().iterator();
        while( sIt.hasNext() ) {

            Stmt s = (Stmt) sIt.next();

            FlowSet liveVariables = (FlowSet) a.getFlowAfter( s );

            // Add StringTags listing live variables
            Iterator variableIt = liveVariables.iterator();
            while( variableIt.hasNext() ) {

                final Value variable = (Value) variableIt.next();

                StringTag t = new StringTag(
                        "Live variable: "+variable );
                s.addTag( t );
            }

            // Add ColorTags to boxes holding live/dead variables
            Iterator boxIt = s.getUseAndDefBoxes().iterator();
            while( boxIt.hasNext() ) {

                final ValueBox vb = (ValueBox) boxIt.next();
                final Value v = vb.getValue();

                if( v instanceof Local ) {
                    if( liveVariables.contains(v) ) {
                        vb.addTag(new ColorTag(ColorTag.GREEN, "Liveness"));
                    } else {
                        vb.addTag(new ColorTag(ColorTag.RED, "Liveness"));
                    }
                }
            }
        }

        // Add a legend for the ColorTags
        SootClass cl = b.getMethod().getDeclaringClass();
        if(!cl.hasTag("KeyTag")) {
            cl.addTag(new KeyTag(ColorTag.GREEN, "Live", "Liveness"));
            cl.addTag(new KeyTag(ColorTag.RED, "Dead", "Liveness"));
        }
    }
}

