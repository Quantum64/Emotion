package co.q64.emotion.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.auto.factory.AutoFactory;

import lombok.Getter;

@AutoFactory
public class AST implements ASTNode {
	private @Getter List<ASTNode> nodes = new ArrayList<>();
	
	protected @Inject AST() {}

	public void add(ASTNode node) {
		nodes.add(node);
	}

	@Override
	public void enter() {
		for (ASTNode node : nodes) {
			node.enter();
		}
	}

	@Override
	public String toString() {
		return "[" + nodes.stream().map(ASTNode::toString).collect(Collectors.joining(", ")) + "]";
	}
}
