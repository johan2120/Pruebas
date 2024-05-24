import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class SimpleInterpreter extends SimpleBaseVisitor<Object> {
    private final Map<String, Object> variables = new HashMap<>();

    public void SimpleVisitor() {
        // Inicializamos constantes PI y E
        variables.put("PI", Math.PI);
        variables.put("E", Math.E);

        // Asignamos la función "Write"
        variables.put("Write", (Function<Object[], Object>) this::write); 
    }

    /**
     * @param args
     * @return
     */
    private Object write(Object[] args) {
        System.out.println("DEBUG: write llamado con argumentos:"); 
        for (Object arg : args) {
            System.out.println(arg); 
        }
        return null;
    }

@Override
public Object visitProgram(SimpleParser.ProgramContext ctx) {
    super.visitProgram(ctx); // Visitar cada línea del programa

    // Imprimir resultados finales aquí (valores de variables, etc.)
    System.out.println("Resultados finales:");
    for (Map.Entry<String, Object> entry : variables.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
    }

    return null;
}


    @Override
    public Object visitFunctionCall(SimpleParser.FunctionCallContext ctx) {
        String name = ctx.IDENTIFIER().getText(); 

        // Verificar si la función existe
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Function " + name + " is not defined");
        }

        // Verificar si la variable es una función
        Object funcObj = variables.get(name);
        if (!(funcObj instanceof Function)) { 
            throw new RuntimeException("Variable " + name + " is not a function");
        }

        // Convertir la variable a una función y llamarla
        @SuppressWarnings("unchecked") // Necesario debido al casting
        Function<Object[], Object> func = (Function<Object[], Object>) funcObj;
        Object[] args = ctx.expression().stream().map(this::visit).toArray();
        return func.apply(args); 
        }
    

    








    @Override
    public Object visitAssignment(SimpleParser.AssignmentContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        Object value = visit(ctx.expression());
        variables.put(varName, value);
        System.out.println("DEBUG: visitAssignment - varName: " + varName + ", value: " + value);
        return null; 
    }



@Override
public Object visitIdentifierExpression(SimpleParser.IdentifierExpressionContext ctx) {
    String varName = ctx.IDENTIFIER().getText();
    if (!variables.containsKey(varName)) {
        throw new RuntimeException("Variable " + varName + " is not defined"); 
    }
    return variables.get(varName);  // Return the value directly
}

@Override
public Object visitConstant(SimpleParser.ConstantContext ctx) {
    if (ctx.INTEGER() != null) {
        return Integer.parseInt(ctx.INTEGER().getText());
    }

    if (ctx.FLOAT() != null) {
        return Float.parseFloat(ctx.FLOAT().getText());
    }

    if (ctx.STRING() != null) {
String texto = ctx.STRING().getText();
return texto.substring(1, texto.length() - 1);
    }

    if (ctx.BOOL() != null) {
        return Boolean.parseBoolean(ctx.BOOL().getText());
    }

    if (ctx.NULL() != null) {
        return null;
    }

    throw new UnsupportedOperationException("Tipo de constante no manejado"); 
}

@Override
public Object visitAdditiveExpression(SimpleParser.AdditiveExpressionContext ctx) {
    Object left = visit(ctx.expression(0));
    Object right = visit(ctx.expression(1));

    String op = ctx.addOp().getText(); 

    switch (op) {
        case "+":
            return add(left, right);
        case "-":
           // return subtract(left, right);
        default:
            throw new UnsupportedOperationException("Operación no soportada: " + op);
    }
}

private Object add(Object left, Object right) {
    if (left instanceof Integer && right instanceof Integer) {
        return (Integer) left + (Integer) right;
    }

    if (left instanceof Float && right instanceof Float) {
        return (Float) left + (Float) right;
    }

    if (left instanceof Integer && right instanceof Float) {
        return (Integer) left + (Float) right;
    }

    if (left instanceof Float && right instanceof Integer) {
        return (Float) left + (Integer) right;
    }

    if (left instanceof String || right instanceof String) {
        return left.toString() + right.toString();
    }

    throw new RuntimeException("No se pueden sumar estos valores.");
}


@Override
public Object visitWhileBlock(SimpleParser.WhileBlockContext ctx) {
    java.util.function.Predicate<Object> condition = 
        ctx.WHILE().getText().equals("while") ? this::isTrue : this::isFalse;

    if (condition.test(visit(ctx.expression()))) {
        do {
            visit(ctx.block());
        } while (condition.test(visit(ctx.expression())));
    } else {
        visit(ctx.elseIfBlock()); 
    }

    return null;
}




@Override
public Object visitComparisonExpression(SimpleParser.ComparisonExpressionContext ctx) {
    Object left = visit(ctx.expression(0));
    Object right = visit(ctx.expression(1));

    String op = ctx.compareOp().getText();

    switch (op) {
        case "<":
            return lessThan(left, right);
        case ">":
            return GreaterThan(left, right);
        default:
            throw new UnsupportedOperationException("Operador no implementado: " + op);
    }
}

private boolean lessThan(Object left, Object right) {
    if (left instanceof Integer && right instanceof Integer) {
        return (Integer) left < (Integer) right;
    }

    if (left instanceof Float && right instanceof Float) {
        return (Float) left < (Float) right;
    }

    if (left instanceof Integer && right instanceof Float) {
        return (Integer) left < (Float) right;
    }

    if (left instanceof Float && right instanceof Integer) {
        return (Float) left < (Integer) right;
    }
    
    throw new RuntimeException("No se pueden comparar estos valores.");
}

private boolean GreaterThan(Object left, Object right) {
    if (left instanceof Integer && right instanceof Integer) {
        return (Integer) left > (Integer) right;
    }

    if (left instanceof Float && right instanceof Float) {
        return (Float) left > (Float) right;
    }

    if (left instanceof Integer && right instanceof Float) {
        return (Integer) left > (Float) right;
    }

    if (left instanceof Float && right instanceof Integer) {
        return (Float) left > (Integer) right;
    }
    
    throw new RuntimeException("No se pueden comparar estos valores.");
}



private boolean isTrue(Object value) {
    if (value instanceof Boolean) {
        return (Boolean) value;
    }
    throw new RuntimeException("Valor no válido"); 
}

public boolean isFalse(Object value) {
    return !isTrue(value);
}



@Override
public Object visitIfBlock(SimpleParser.IfBlockContext ctx) {
    if (isTrue(visit(ctx.expression()))) {
        visit(ctx.block()); // Imprimir resultados solo si la condición es verdadera
    } else {
        visit(ctx.elseIfBlock()); // Lo mismo para el bloque else-if
    }
    return null;
}

}