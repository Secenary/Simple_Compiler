package cn.edu.yali.compiler.parser.table;

import cn.edu.yali.compiler.lexer.Token;

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
