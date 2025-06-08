package io.github.thugborean.syntax;

public enum TokenType {
    // Literals
    NumericLiteral,
    DoubleLiteral,
    StringLiteral,
    CharLiteral,
    BoolLiteral,
    NullLiteral,

    // Identifier
    Identifier,

    // Operators
    Assign, // =
    Plus, // +
    Minus, // -
    Multiply, // *
    Divide, // /
    Modulo, // %

    DoubleQuote, // "
    SingleQuote, // '

    Export, // Used on struct members to modify their visibility
    Global, // Used on struct members to modify their visibility
    Hidden, // Used on struct members to modify their visibility

    // Two-character operators
    PlusPlus, // ++
    MinusMinus, // --
    AtseriskAsterisk, // **
    EqualsEquals, // ==

    Append, // +=
    Truncate, // -=

    Return, // ->

    // Variables
    Number, // num
    Double, // double
    String, // string
    Character, // char
    Bool, // bool

    // Functions and structures
    Function, // func
    Void, // Used for returnType in functions
    Structure, // struct

    // Loops and conditionals
    If,
    LessThan,
    GreaterThan,
    LessThanOrEquals,
    GreaterThanOrEquals,
    While,
    For,
    Do,
    True,
    False,

    // Scope
    OpenBracket,
    ClosedBracket,
    OpenParenthesis,
    CloseParenthesis,
    OpenCurly,
    ClosedCurly,
    SemiColon,
    Dot, // .

    // EOF
    EOF,
}
