package co.q64.emotion.ast;

import co.q64.emotion.lang.Iterator;
import co.q64.emotion.lang.Program;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

@AutoFactory
public class ASTIterator implements ASTNode {
    private Provider<Iterator> iteratorProvider;
    private Program program;
    private AST nodes;
    private boolean stack;

    protected @Inject ASTIterator(@Provided Provider<Iterator> iteratorProvider, @Nullable Program program, AST nodes, boolean stack) {
        this.iteratorProvider = iteratorProvider;
        this.program = program;
        this.nodes = nodes;
        this.stack = stack;
    }

    @Override
    public ASTBackpropagation enter() {
        Iterator itr = iteratorProvider.get().setup(program, stack);
        loop:
        while (!itr.next()) {
            if (!program.shouldContinueExecution()) {
                break;
            }
            for (ASTNode node : nodes.getNodes()) {
                switch (node.enter()) {
                    case BREAK:
                        break loop;
                    case CONTINUE:
                        continue loop;
                    default:
                        break;
                }
            }
        }
        return ASTBackpropagation.NONE;
    }

    @Override
    public String toString() {
        return "(iterate " + nodes.toString() + ")";
    }
}
