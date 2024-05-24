import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String inputFile = "C:\\Users\\johan\\Downloads\\javavvv\\proyecto_compiladores\\src\\main\\java\\test.ss";
        CharStream input = CharStreams.fromFileName(inputFile);


        SimpleLexer lexer = new SimpleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleParser parser = new SimpleParser(tokens);

        ParseTree tree = parser.program(); 

        SimpleInterpreter interpreter = new SimpleInterpreter();
        interpreter.visit(tree); // Iniciar la interpretación
    }
}
