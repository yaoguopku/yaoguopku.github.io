package olhotak.liveness;
import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;


// STEP 1: Decide whether it is a backwards or forwards analysis.
// BACKWARDS
//
// STEP 2: What are the lattice elements?
// SETS OF VARIABLES => Use ArraySparseSet.
// ordered by set inclusion
//
//
class LiveVariablesAnalysis extends BackwardFlowAnalysis
{
    private UnitGraph g;
    LiveVariablesAnalysis(UnitGraph g)
    {
        super(g);
        this.g = g;

        doAnalysis();
        
    }

// STEP 3: What is the merge operator?
// UNION
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1;
        FlowSet inSet2 = (FlowSet) in2;
        FlowSet outSet = (FlowSet) out;

        inSet1.union(inSet2, outSet);
    }
    

// STEP 4: Define flow equations.
// in(s) = ( out(s) minus defs(s) ) union uses(s)
//
    protected void flowThrough(Object outValue, Object unit,
            Object inValue)
    {
        FlowSet in  = (FlowSet) inValue;
        FlowSet out = (FlowSet) outValue;
        Stmt    s   = (Stmt)    unit;

        // Copy out to in
        out.copy( in );

        // Take out kill set
        Iterator boxIt = s.getDefBoxes().iterator();
        while( boxIt.hasNext() ) {
            final ValueBox box = (ValueBox) boxIt.next();
            Value value = box.getValue();
            if( value instanceof Local )
                in.remove( value );
        }

        // Add gen set
        boxIt = s.getUseBoxes().iterator();
        while( boxIt.hasNext() ) {
            final ValueBox box = (ValueBox) boxIt.next();
            Value value = box.getValue();
            if( value instanceof Local )
                in.add( value );
        }
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source;
        FlowSet destSet   = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }

// STEP 5: Determine value for start/end node.
// STEP 6: Initial approximation (bottom).
//
// end node:              empty set
// initial approximation: empty set
    protected Object entryInitialFlow()
    {
        return new ArraySparseSet();
    }
        
    protected Object newInitialFlow()
    {
        return new ArraySparseSet();
    }
        
}
