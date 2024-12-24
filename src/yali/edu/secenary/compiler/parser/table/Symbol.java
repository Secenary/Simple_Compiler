package yali.edu.secenary.compiler.parser.table;

import yali.edu.secenary.compiler.lexer.Token;

public class Symbol {
    public Token token;
    NonTerminal nonTerminal;
    private Symbol(Token token, NonTerminal nonTerminal){
        this.token = token;
        this.nonTerminal = nonTerminal;
    }
    public Symbol(Token token){
        this(token, null);
    }
    public Symbol(NonTerminal nonTerminal){
        this(null, nonTerminal);
    }
    public boolean isToken(){
        return this.token != null;
    }
    public boolean isNonterminal(){
        return this.nonTerminal != null;
    }
}
