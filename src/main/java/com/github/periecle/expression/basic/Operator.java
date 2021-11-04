package com.github.periecle.expression.basic;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public enum Operator implements Comparable<Operator> {

  ADDITION("+", Associativity.LEFT, 1, Double::sum),
  SUBTRACTION("-", Associativity.RIGHT, 1, (first, second) -> first - second),
  DIVISION("/", Associativity.LEFT, 2, (first, second) -> first / second),
  MULTIPLICATION("*", Associativity.LEFT, 2, (first, second) -> first * second),
  MODULUS("%", Associativity.LEFT, 2, (first, second) -> first % second),
  POWER("^", Associativity.RIGHT, 3, Math::pow);

  final String symbol;
  final Associativity associativity;
  final int precedence;
  final BinaryOperator<Double> mapper;

  Operator(final String symbol, final Associativity associativity, final int precedence, final BinaryOperator<Double> mapper) {
    this.symbol = symbol;
    this.associativity = associativity;
    this.precedence = precedence;
    this.mapper = mapper;
  }

  public int comparePrecedence(final Operator operator) {
    return this.precedence - operator.precedence;
  }

  public String getSymbol() {
    return symbol;
  }

  public Associativity getAssociativity() {
    return associativity;
  }

  public int getPrecedence() {
    return precedence;
  }

  public BinaryOperator<Double> getMapper() {
    return mapper;
  }
}
