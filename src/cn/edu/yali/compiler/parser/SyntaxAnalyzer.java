package cn.edu.yali.compiler.parser;

import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.*;
import cn.edu.yali.compiler.symtab.SymbolTable;
import java.util.*;

//Experiment 2: Implementing LR parsing driver

/**
 * LR grammar analysis driver
 * <br>
 * This program accepts lexical unit strings and LR analysis tables (action and goto tables), analyzes the lexical unit stream according to the table, executes the corresponding actions, and notifies each registered observer when executing the action.
 * <br>
 * You should implement the corresponding method according to the documentation of the hollowed-out method. You can add private member objects you need to the class at will, but you should not add public interfaces to this class, nor should you modify the methods that are not hollowed out.
 * Unless you have fully communicated with the teaching assistant and can prove the rationality of your modification, and let the teaching assistant determine the evaluation method that may be modified. Modifying other parts of the class at will may cause automatic evaluation errors and deductions.
*/
public class SyntaxAnalyzer {
    private final SymbolTable symbolTable;
    private final List<ActionObserver> observers = new ArrayList<>();
    private final List<Token> lr1_tokens = new ArrayList<>();
    private LRTable lr1_table;
    private Stack<Status> status_stack = new Stack<>();
    private Stack<Symbol> token_stack = new Stack<>();

    public SyntaxAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * Registering a new observer
     *
     * @param observer Observer
     */
    public void registerObserver(ActionObserver observer) {
        observers.add(observer);
        observer.setSymbolTable(symbolTable);
    }

    /**
     * Notify observers when a shift action is performed
     *
     * @param currentStatus currentStatus
     * @param currentToken  currentToken
     */
    public void callWhenInShift(Status currentStatus, Token currentToken) {
        for (final var listener : observers) {
            listener.whenShift(currentStatus, currentToken);
        }
    }

    /**
     * Notify observers when a reduce action is performed
     *
     * @param currentStatus currentStatus
     * @param production    production
     */
    public void callWhenInReduce(Status currentStatus, Production production) {
        for (final var listener : observers) {
            listener.whenReduce(currentStatus, production);
        }
    }

    /**
     * Notify observers when an accept action is performed
     *
     * @param currentStatus currentStatus
     */
    public void callWhenInAccept(Status currentStatus) {
        for (final var listener : observers) {
            listener.whenAccept(currentStatus);
        }
    }

    public void loadTokens(Iterable<Token> tokens) {
        // Load tokens
        // You can choose how to store tokens, such as using iterators, stacks, or simply using a list to store them all
        // Note that when implementing the driver, you will need to face the situation where you can only read a token but not consume it.
        // Please consider this situation when designing your own
        for(var token: tokens)
            lr1_tokens.add(token);
    }

    public void loadLRTable(LRTable table) {
        // Load LR analysis table
        // You can choose how to use the table:
        // Call getAction/getGoto directly on LRTable, or save initStatus directly for use
        lr1_table = table;
    }

    public void run() {
        // Implement the driver program
        // You need to implement the driver program for LR parsing based on the above input
        // Please call the above callWhenInShift, callWhenInReduce, callWhenInAccept when encountering Shift, Reduce, Accept respectively
        // Otherwise, the production output used for scoring experiment 2 may not work properly
        int cnt = 0;
        Status curStatus = lr1_table.getInit();
        status_stack.push(curStatus);
        while(cnt != lr1_tokens.size()) {
            Token next_token = lr1_tokens.get(cnt);
            if (status_stack.peek().getAction(next_token).getKind().toString().equals("Shift")) {
                curStatus = status_stack.peek();
                status_stack.push(curStatus.getAction(next_token).getStatus());
                token_stack.push(new Symbol(next_token));
                callWhenInShift(curStatus, next_token);
                cnt++;
            }
            else if(status_stack.peek().getAction(next_token).getKind().toString().equals("Reduce")) {
                callWhenInReduce(status_stack.peek(), status_stack.peek().getAction(next_token).getProduction());
                String[] tmp_string = status_stack.peek().getAction(next_token).toString().split(" ");
                for(int i = 1; i <= tmp_string.length - 3; i++) {
                    status_stack.pop();
                    token_stack.pop();
                }
                token_stack.push(new Symbol(new NonTerminal(tmp_string[1])));
                status_stack.push(status_stack.peek().getGoto(new NonTerminal(tmp_string[1])));
            }
            else if(status_stack.peek().getAction(next_token).getKind().toString().equals("Accept")) {
                callWhenInAccept(status_stack.peek());
                break;
            }
        }
    }
}
