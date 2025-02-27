package cn.edu.yali.compiler.parser;


import cn.edu.yali.compiler.lexer.Token;
import cn.edu.yali.compiler.parser.table.Production;
import cn.edu.yali.compiler.parser.table.Status;
import cn.edu.yali.compiler.symtab.SymbolTable;

/**
 * LR driver action observer
 * <br>
 * This interface abstracts the concept of action observer, decoupling the LR driver from specific semantic actions, allowing it to handle arbitrary, abstract grammars, and separating the specific operation of "execute something when encountering a certain production"
 * from the driver.
 * <br>
 * In traditional parser generators, such as yacc, the actions corresponding to each grammar specification are generally written directly in the .y file; and since our parser cannot directly write code actions in grammar.txt
 * , we can only implement SDT by dispatching actions according to the different productions passed in in our own code implementation. For an example of using this interface, please refer to
 * ProductionCollector class
 * <br>
 * Note that observers cannot access the state stack maintained by the LR driver, and the stack information maintained by observers should not access each other. Each observer that implements this interface needs to define the state information it needs and maintain its own state stack.
 *
 * @see ProductionCollector
 * @see SyntaxAnalyzer
 */
public interface ActionObserver {
    /**
     * This function is called when the driver performs a Shift action. The state that Shift will transfer to can be obtained directly from the parameters:
     * {@code currentStatus.getAction(currentToken).getStatus() }
     *
     * @param currentStatus current state
     * @param currentToken current token
     */
    void whenShift(Status currentStatus, Token currentToken);

    /**
     * This function is called when the driver performs a Reduce action. The new state to which Goto is applied can be obtained directly from the parameters:
     * {@code currentStatus.getGoto(production.head()) }
     *
     * @param currentStatus current state
     * @param production production to be reduced
     */
    void whenReduce(Status currentStatus, Production production);

    /**
     * This function is called when the driver performs the Accept action.
     *
     * @param currentStatus current status
     */
    void whenAccept(Status currentStatus);

    /**
     * This function is called when the driver accepts the symbol table. The class that implements this interface can decide whether to store this symbol table.
     *
     * @param table symbol table
     */
    void setSymbolTable(SymbolTable table);
}
