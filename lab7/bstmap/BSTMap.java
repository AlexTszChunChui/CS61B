package bstmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    int size = 0;
    BSTNode root = null;
    
    public class BSTNode {
        public K key;
        public V val;
        private BSTNode LeftNode = null;
        private BSTNode RightNode = null;
        private BSTNode parent;
        
        public BSTNode(K Key, V Val, BSTNode Parent) {
            this.key = Key;
            this.val = Val;
            this.parent = Parent;
        }

        public void put(K Key, V Val) {
            int compare = this.key.compareTo(Key);
            if (compare == 0) {
                this.val = Val;
            } else if (compare < 0 && LeftNode == null) {
                LeftNode = new BSTNode(Key, Val, this);
            } else if (compare > 0 && RightNode == null) {
                RightNode = new BSTNode(Key, Val, this);
            } else if (compare < 0) {
                LeftNode.put(Key, Val);
            } else {
                RightNode.put(Key, Val);
            }
        }

        /**public BSTNode get(BSTNode N, K key) {
            if (N == null) {
                return null;
            }
            int compare = N.key.compareTo(key);
            if (compare == 0) {
                return N;
            } else if (compare < 0) {
                return get(N.LeftNode, key);
            } else {
                return get(N.RightNode, key);
            }
        }*/

        public BSTNode get(K key) {
            int compare = this.key.compareTo(key);
            if (compare == 0) {
                return this;
            } else if (this.is_leaf() || compare < 0 && LeftNode == null || compare > 0 && RightNode == null) {
                return null;
            } else if (compare < 0) {
                return LeftNode.get(key);
            } else {
                return RightNode.get(key);
            }
        }

        public boolean is_leaf() {
            return (LeftNode == null && RightNode == null);
        }    

        public boolean branches() {
            return (! is_leaf());
        }

        public String printInOrder() {
            String mapping = String.format("%s : %s", this.key, this.val);
            if (is_leaf()) {
                return mapping;
            }else if (LeftNode == null) {
                return mapping + ", "  + RightNode.printInOrder();
            }else if (RightNode == null) {
                return LeftNode.printInOrder() + ", " + mapping;
            }else {
                return LeftNode.printInOrder() + ", " + mapping + ", " + RightNode.printInOrder();
            }
        }

        public ArrayList<K> Keylst() {
            ArrayList<K> Key = new ArrayList<>();
            Key.add(this.key);
            if (is_leaf()) {
                return Key;
            }else if (LeftNode == null) {
                Key.addAll(RightNode.Keylst());
                return Key;
            }else if (RightNode == null) {
                ArrayList<K> Left = LeftNode.Keylst();
                Left.addAll(Key);
                return Left;
            }else {
                ArrayList<K> Left = LeftNode.Keylst();
                Left.addAll(Key);
                Left.addAll(RightNode.Keylst());
                return Left;
            }
        }

        public void addtree(BSTNode node) {
            if (node == null) {
                return;
            }
            put(node.key, node.val);
            addtree(node.LeftNode);
            addtree(node.RightNode);
        }

        public BSTNode findreplacement() {
            if (is_leaf()) {
                return null;
            } else if (LeftNode == null) {
                return RightNode.findsmallest();
            } else {
                return LeftNode.findbiggest();
            }
        }

        private BSTNode findbiggest() {
            if (RightNode == null) {
                return this;
            }
            return RightNode.findbiggest();
        }

        private BSTNode findsmallest() {
            if (LeftNode == null) {
                return this;
            }
            return LeftNode.findsmallest();
        }

    }
    public void printInOrder() {
        String inorder = root.printInOrder();
        System.out.println("{" + inorder + "}");
    }

    public ArrayList<K> Keylst() {
        if (root == null) {
            return null;
        } return root.Keylst();
    }


    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(key) != null;
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        BSTNode node = root.get(key);
        return node.val;
    }

    @Override
    public int size() {
        return this.size;
    }

    public void put(K key, V val) {
       if (root == null) {
           root = new BSTNode(key, val, null);
       } else {
           root.put(key, val);
       }
       size += 1;
    }

    @Override
    public Set keySet() {
        return new HashSet<K>(root.Keylst());
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }
        size -= 1;
        BSTNode rm = root.get(key);
        BSTNode replacement = rm.findreplacement();

        if (key == root.key) {
            root = replacement;
        } else if (rm.parent.LeftNode == rm) {
            rm.parent.LeftNode = replacement;
        } else {
            rm.parent.RightNode = replacement;
        }

        if (replacement == null) {
            return rm.val;
        }
        replacement.addtree(rm.LeftNode);
        replacement.addtree(rm.RightNode);
        replacement.parent = rm.parent;
        return rm.val;
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key)) {
            return null;
        } else if (!root.get(key).val.equals(value)) {
            return null;
        } else return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIter();
    }

    private class BSTMapIter implements Iterator<K> {

        private Iterator<K> iter;

        public BSTMapIter() {
            iter = keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public K next() {
            return iter.next();
        }
    }
}
