import randoop.CheckRep;

import java.util.*;

public class AvlTree<K extends Comparable<K>, V> {
    private AvlNode root;

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size(root);
    }

    private int size(AvlNode node) {
        if (node == null) return 0;
        return node.size;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public V get(K key) {
        AvlNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node.value;
            }
        }
        return null;
    }

    public void put(K key, V value) {
        root = put(root, key, value);
    }

    private AvlNode put(AvlNode node, K key, V value) {
        if (node == null) {
            return new AvlNode(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }
        node.updateHeightAndSize();
        return balance(node);
    }

    public void remove(K key) {
        root = remove(root, key);
    }

    private AvlNode remove(AvlNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = remove(node.left, key);
        } else if (cmp > 0) {
            node.right = remove(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            } else {
                AvlNode temp = node;
                node = min(temp.right);
                node.right = deleteMin(temp.right);
                node.left = temp.left;
            }
        }
        node.updateHeightAndSize();
        return balance(node);
    }

    private AvlNode min(AvlNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private AvlNode deleteMin(AvlNode node) {
        if (node.left == null) return node.right;
        node.left = deleteMin(node.left);
        node.updateHeightAndSize();
        return balance(node);
    }

    private AvlNode balance(AvlNode node) {
        int balanceFactor = balanceFactor(node);
        if (balanceFactor > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        } else if (balanceFactor < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            node = rotateLeft(node);
        }
        return node;
    }

    private int balanceFactor(AvlNode node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    private int height(AvlNode node) {
        if (node == null) return 0;
        return node.height;
    }

    private AvlNode rotateRight(AvlNode x) {
        AvlNode y = x.left;
        x.left = y.right;
        y.right = x;
        x.updateHeightAndSize();
        y.updateHeightAndSize();
        return y;
    }

    private AvlNode rotateLeft(AvlNode x) {
        AvlNode y = x.right;
        x.right = y.left;
        y.left = x;
        x.updateHeightAndSize();
        y.updateHeightAndSize();
        return y;
    }

    public Iterable<K> keys() {
        List<K> keys = new ArrayList<>();
        inorder(root, keys);
        return keys;
    }

    private void inorder(AvlNode node, List<K> keys) {
        if (node == null) return;
        inorder(node.left, keys);
        keys.add(node.key);
        inorder(node.right, keys);
    }

    @CheckRep
    // Public entry point, no arguments
    public boolean repOK() {
        return checkInvariants(root, null, null);
    }

    // Private recursive method implementing the actual invariant checks
    private boolean checkInvariants(AvlNode node, K min, K max) {
        if (node == null) return true;

        // 1. Check BST property
        if (min != null && node.key.compareTo(min) <= 0) return false;
        if (max != null && node.key.compareTo(max) >= 0) return false;

        // 2. Check AVL balance condition
        int balance = height(node.left) - height(node.right);
        if (balance < -1 || balance > 1) return false;

        // 3. Verify height and size are calculated correctly
        if (node.height != 1 + Math.max(height(node.left), height(node.right))) return false;
        if (node.size != 1 + size(node.left) + size(node.right)) return false;

        // Recursively check left and right subtrees
        return checkInvariants(node.left, min, node.key)
                && checkInvariants(node.right, node.key, max);
    }

    private class AvlNode {
        private K key;
        private V value;
        private int height;
        private int size;
        private AvlNode left;
        private AvlNode right;

        public AvlNode(K key, V value, int size) {
            this.key = key;
            this.value = value;
            this.size = size;
            this.height = 1;
        }

        private void updateHeightAndSize() {
            height = 1 + Math.max(height(left), height(right));
            size = 1 + size(left) + size(right);
        }
    }

}
