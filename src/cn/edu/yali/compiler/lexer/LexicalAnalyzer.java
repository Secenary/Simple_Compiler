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
 * TODO: 实验一: 实现词法分析
 * <br>
 * 你可能需要参考的框架代码如下:
 *
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
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
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) {
        // TODO: 词法分析前的缓冲区实现
        // 可自由实现各类缓冲区
        // 或直接采用完整读入方法
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            content = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表 <br>
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // TODO: 自动机实现的词法分析过程
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
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public Iterable<Token> getTokens() {
        // TODO: 从词法分析过程中获取 Token 列表
        // 词法分析过程可以使用 Stream 或 Iterator 实现按需分析
        // 亦可以直接分析完整个文件
        // 总之实现过程能转化为一列表即可
        return tokens;
    }



    public void dumpTokens(String path) {

        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
