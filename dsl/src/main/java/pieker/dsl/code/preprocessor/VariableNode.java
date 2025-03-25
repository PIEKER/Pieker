package pieker.dsl.code.preprocessor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pieker.dsl.code.exception.VariableConfigurationException;

import java.util.*;
import java.util.regex.Pattern;

import static pieker.dsl.util.CodeUtil.extractVariablesFromData;

@Getter
@Slf4j
public class VariableNode {

    @Setter
    private String value;
    private final String identifier;
    private final Set<VariableNode> childNodes = new HashSet<>();
    private final Set<VariableNode> parentNodes = new HashSet<>();

    public VariableNode(String identifier, String value){
        this.identifier =  identifier;
        this.value = value;
    }

    /**
     * Constructs a dependency graph of the all variables provided in the argument.
     *
     * @param nodeMap Map<String, VariableNode> of all variables participating in the graph
     */
    public static void constructVariableGraph(Map<String, VariableNode> nodeMap){
        for (VariableNode node: nodeMap.values()){
            Set<String> variablesInValue = extractVariablesFromData(node.value);
            for (String variableInValue: variablesInValue){
                if (!nodeMap.containsKey(variableInValue)){
                    throw new VariableConfigurationException("Unknown variable detected! Variable: " + variableInValue);
                }

                VariableNode parent = nodeMap.get(variableInValue);
                parent.childNodes.add(node);
                node.parentNodes.add(parent);
            }
        }
    }

    /**
     * Resolves all referenced variables in variable-values. Uses topological sorting to process a provided
     * dependency graph.
     *
     * @param nodeMap Map<String, VariableNode> all variables linked to a dependency graph
     */
    public static void injectVariablesOfGraph(Map<String, VariableNode> nodeMap){
        PriorityQueue<VariableNode> priorityQueue =
                new PriorityQueue<>(Comparator.comparingInt(node -> node.parentNodes.size()));

        // Add all variables with no dependencies
        priorityQueue.addAll(nodeMap.values().stream()
                .filter(node -> node.parentNodes.isEmpty()).toList());

        while (!priorityQueue.isEmpty()){
            VariableNode node = priorityQueue.poll();
            node.childNodes.forEach(childNode -> {
                // inject variable
                childNode.value = childNode.value.replaceAll(
                        Pattern.quote(node.identifier), node.value);

                // remove dependency from child nodes
                childNode.parentNodes.remove(node);
                if (childNode.parentNodes.isEmpty()){
                    // if variable has no dependencies anymore, add for further processing
                    priorityQueue.add(childNode);
                }
            });
        }

        nodeMap.values().forEach(node -> {
            if (!node.parentNodes.isEmpty()){
                throw new VariableConfigurationException("cyclic variable declaration detected. " +
                        "Unable to resolve values for '" + node.identifier + "'.");
            }
        });
    }
}

