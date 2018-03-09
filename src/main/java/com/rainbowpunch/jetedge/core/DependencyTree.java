package com.rainbowpunch.jetedge.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: 3/9/18 Flush out
public class DependencyTree {

    private Node parentNode;

    public DependencyTree(Class clazz) {
        parentNode = new Node(clazz);
    }

    public void graftNode(DependencyTree treeToGraft, String name) {
        parentNode.graft(treeToGraft.parentNode, name);
    }

    public void addNode(String fullPathName, Class clazz) {
        parentNode.add(fullPathName, clazz);
    }

    private static class Node {

        private static Pattern tailPattern = Pattern.compile("(.*)\\.(.*)$");

        private String name;
        private Class clazz;
        private Map<String, Node> children;

        public Node(Class clazz) {
            this("", clazz);
        }

        public Node(String name, Class clazz) {
            this.name = name;
            this.clazz = clazz;
            children = new HashMap<>();
        }

        public void graft(Node node, String name) {
            this.name = name;
            node.children.put(this.name, this);
        }

        public void add(String name, Class clazz) {
            Node underNode = this;
            String thisName = name;
            if (name.contains(".")) {
                Matcher matcher = tailPattern.matcher(name);
                if (!matcher.matches()) {
                    throw new RuntimeException("Something is amiss");
                }
                underNode = searchForNode(matcher.group(1), this);
                thisName = matcher.group(2);
            }
            underNode.children.put(thisName, new Node(thisName, clazz));
        }

        private Node searchForNode(String name, Node node) {
            Node returnNode = null;
            if (name.contains(".")) {
                String[] separatedString = name.split("\\.", 2);
                returnNode = searchForNode(separatedString[1], node);
            } else {
                if (!node.children.containsKey(name)) throw new RuntimeException("MISSING");
                returnNode = node.children.get(name);
            }
            return returnNode;
        }

    }


}
