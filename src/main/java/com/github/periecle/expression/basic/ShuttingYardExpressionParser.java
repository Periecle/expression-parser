package com.github.periecle.expression.basic;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.github.periecle.expression.basic.Associativity.LEFT;
import static com.github.periecle.expression.basic.Associativity.RIGHT;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class ShuttingYardExpressionParser {

 private static final Map<String, Operator> OPERATOR_MAPPING = Arrays.stream(Operator.values())
   .collect(toMap(Operator::getSymbol, identity()));

 private static final Predicate<String> isOperator = OPERATOR_MAPPING::containsKey;

 private static final BiPredicate<Operator, Operator> validLeftAssociativityNestedExpression =
   (current, previous) -> (current.associativity == LEFT && current.comparePrecedence(previous) <= 0);

  private static final BiPredicate<Operator, Operator> validRightAssociativityNestedExpression =
    (current, previous) -> (current.associativity == RIGHT && current.comparePrecedence(previous) < 0);

  private static final BiPredicate<Operator, Operator> validNestedOperatorExpression = validLeftAssociativityNestedExpression.or(validRightAssociativityNestedExpression);

  public static List<String> convertTokensToInfixNotation(final List<String> tokens) {

    final List<String> output = new LinkedList<>();
    final Deque<String> stack = new ArrayDeque<>();

    for (String token : tokens) {
      if (isOperator.test(token)) {
        while (!stack.isEmpty() && isOperator.test(stack.peek())) {
          Operator cOp = OPERATOR_MAPPING.get(token); // Current operator
          Operator lOp = OPERATOR_MAPPING.get(stack.peek()); // Top operator from the stack
          if (validNestedOperatorExpression.test(cOp, lOp)) {

            output.add(stack.pop());
            continue;
          }
          break;
        }
        stack.push(token);
      } else if ("(".equals(token)) {
        stack.push(token);
      } else if (")".equals(token)) {
        while (!stack.isEmpty() && !stack.peek().equals("(")) {
          output.add(stack.pop());
        }
        stack.pop();
      } else {
        output.add(token);
      }
    }

    while (!stack.isEmpty()) {
      output.add(stack.pop());
    }

    return output;
  }


  public static double reversePolishNotationToResult(List<String> tokens) {
    final Deque<String> stack = new ArrayDeque<>();

    // For each token
    for (String token : tokens) {
      // If the token is a value push it onto the stack
      if (!isOperator.test(token)) {
        stack.push(token);
      } else {
        // Token is an operator: pop top two entries
        final double d2 = Double.parseDouble(stack.pop());
        final double d1 = Double.parseDouble(stack.pop());

        //Get the result
        final double result = applyOperator(token, d1, d2);

        // Push result onto stack
        stack.push(String.valueOf(result));
      }
    }

    return Double.parseDouble(stack.pop());
  }

  private static Double applyOperator(final String operator, final double first, final double second) {
    return OPERATOR_MAPPING.get(operator)
      .mapper
      .apply(first, second);
  }

  public static void main(String[] args) {
    String in = "( 1 + 2 ) * ( 3 / 4 ) ^ 2 - ( - 5 + 6 )";
    List<String> input = Arrays.asList(in.split(" "));
    List<String> output = convertTokensToInfixNotation(input);

    System.out.println("Input: " + in);

    // Build output RPN string minus the commas
    System.out.print("Intermediate: ");
    for (String token : output) {
      System.out.print(token + " ");
    }
    System.out.println();

    // Feed the RPN string to RPNtoDouble to give result
    double result = reversePolishNotationToResult(output);
    System.out.println("Result = " + result);
  }
}
