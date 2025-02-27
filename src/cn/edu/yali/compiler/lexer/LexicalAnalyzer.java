package cn.edu.yali.compiler.lexer;

import cn.edu.yali.compiler.symtab.SymbolTable;
import cn.edu.yali.compiler.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static cn.edu.yali.compiler.lexer.Token.*;

/**
 * Experiment 1: Implement lexical analysis
 * <br>
 * The framework code you may need to refer to is as follows:
 *
 * @see Token lexical unit implementation
 * @see TokenKind lexical unit type implementation
 */
public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private List<Token> tokens = new ArrayList<>();
    String content;

    enum State {
        start,
        state14,
        state16,
        state18,
        state21,
        state24
    }

    private State currentState;

    private StringBuilder tokenBuilder = new StringBuilder();

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }


    /**
     * Read and load the file contents from the given path
     *
     * @param path the given path
     */
    public void loadFile(String path) {
        // Implementation of buffer before lexical analysis
        // You can freely implement various buffers
        // Or directly use the complete reading method
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            content = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform lexical analysis and prepare a token list for return <br>
     * The symbol table entries required for experiment 1 need to be maintained, and the members of the symbol table entries that can only be determined in syntax analysis can be set to null first
     */
    public void run() {
        // Lexical analysis process implemented by automaton
        this.currentState = State.start;
        for(int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            switch(currentState) {
                case State.state14: {
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        tokenBuilder.append(c);
                        continue;
                    } else {
                        if(tokenBuilder.toString().equals("int") || tokenBuilder.toString().equals("return")) {
                            if(tokenBuilder.toString().equals("int"))
                                tokens.add(simple("int"));
                            else if(tokenBuilder.toString().equals("return"))
                                tokens.add(simple("return"));
                        }
                        else if(!tokenBuilder.isEmpty()){
                            String identifierText = tokenBuilder.toString();
                            if(!symbolTable.has(identifierText)) {
                                symbolTable.add(identifierText);
                            }
                            tokens.add(normal("id",tokenBuilder.toString()));
                        }
                        if(c == ';')
                            tokens.add(simple("Semicolon"));
                        tokenBuilder.setLength(0);
                        currentState = State.start;
                        continue;
                    }
                }
                case State.state16: {
                    if(Character.isDigit(c)) {
                        tokenBuilder.append(c);
                        continue;
                    }
                    else {
                        if(!tokenBuilder.isEmpty())
                            tokens.add(normal("IntConst", tokenBuilder.toString()));
                        tokenBuilder.setLength(0);
                        if(c == ';')
                            tokens.add(simple("Semicolon"));
                        currentState = State.start;
                        continue;
                    }
                }
                case State.state18:{
                    if(c == '*') {
                        currentState = State.start;
                        continue;
                    }
                    else {
                        tokens.add(simple("*"));
                        currentState = State.start;
                        continue;
                    }
                }
                case State.state21:{
                    if(c == '=') {
                        currentState = State.start;
                        continue;
                    }
                    else {
                        tokens.add(simple("="));
                        currentState = State.start;
                        continue;
                    }
                }
                case State.state24: {
                    if (Character.isLetter(c) || Character.isDigit(c)) {
                        continue;
                    } else {
                        currentState = State.start;
                        continue;
                    }
                }
                case State.start: {
                    if(c == ' ' || c == '\n') continue;
                    tokenBuilder.setLength(0);
                    if (Character.isLetter(c)) {
                        tokenBuilder.append(c);
                        currentState = State.state14;
                    }
                    else if(Character.isDigit(c)) {
                        tokenBuilder.append(c);
                        currentState = State.state16;
                    }
                    else if(c == '*') {
                        currentState = State.state18;
                    }
                    else if(c == '=') {
                        currentState = State.state21;
                    }
                    else if(c == '"') {
                        currentState = State.state24;
                    }
                    else if(c == '(') {
                        tokens.add(simple("("));
                        currentState = State.start;
                    }
                    else if(c == ')') {
                        tokens.add(simple(")"));
                        currentState = State.start;
                    }
                    else if(c == ':')
                        currentState = State.start;
                    else if(c == '+') {
                        tokens.add(simple("+"));
                        currentState = State.start;
                    }
                    else if(c == '-') {
                        tokens.add(simple("-"));
                        currentState = State.start;
                    }
                    else if(c == '/') {
                        tokens.add(simple("/"));
                        currentState = State.start;
                    }
                    else if(c == ';') {
                        tokens.add(simple("Semicolon"));
                        currentState = State.start;
                    }

                }
            }
        }
        tokens.add(eof());
    }

    /**
     * Get the result of lexical analysis, and make sure to call it after calling the run method
     *
     * @return Token List
     */
    public Iterable<Token> getTokens() {
        // Get the token list from the lexical analysis process
        // The lexical analysis process can use Stream or Iterator to implement on-demand analysis
        // You can also directly analyze the entire file
        // In short, the implementation process can be converted into a list
        return tokens;
    }



    public void dumpTokens(String path) {

        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
