package arithlang;

import java.util.List;
import java.util.Map;

import static arithlang.AST.*;
import static arithlang.Value.*;
import static arithlang.Env.*;

public class Evaluator implements Visitor<Value> {

    Value valueOf(Program p) {
        return (Value) p.accept( this, new EmptyEnv());
    }

    @Override
    public Value visit(Program p, Env env) {
        return (Value) p.e().accept( this, env);
    }

    @Override
    public Value visit(NumExp e, Env env) {
        return e.v();
    }

    @Override
    public Value visit(AddExp e, Env env) {
        // semantics for add expression -- the result is the result of
        // summing each of the operands
        double result = 0;
        List<Exp> operands = e.all();
        for (Exp operand : operands) {
            NumVal val = (NumVal) operand.accept( this, env);
            result += val.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(SubExp e, Env env) {
        // semantics for sub expression -- the result is the result of
        // subtracting each of the operands, in sequence, from left to
        // right
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.get(0).accept( this, env);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept( this, env);
            result = result - rVal.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(MultExp e, Env env) {
        // semantics for mult expression -- the result is the result of
        // multiplying each of the operands
        double result = 1;
        List<Exp> operands = e.all();
        for (Exp operand : operands) {
            NumVal val = (NumVal) operand.accept( this, env);
            result *= val.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(DivExp e, Env env) {
        // semantics for div expression -- the result is the result of
        // dividing each of the operands, in sequence, from left to
        // right
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.get(0).accept( this, env);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept( this, env);
            result = result / rVal.v();
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(IntDivExp e, Env env) {
        // semantics for div expression -- the result is the result of
        // dividing each of the operands, in sequence, from left to
        // right
        List<Exp> operands = e.all();
        NumVal lVal = (NumVal) operands.get(0).accept( this, env);
        double result = lVal.v();
        for (int i = 1; i < operands.size(); i++) {
            NumVal rVal = (NumVal) operands.get(i).accept( this, env);
            result = ((int) (result / rVal.v()));
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(PowExp e, Env env) {
        // semantics for mult expression -- the result is the result of
        // multiplying each of the operands
        double result = 1;
        List<Exp> operands = e.all();
        for (int i=operands.size()-1; i>=0; i--) {
            NumVal val = (NumVal) operands.get(i).accept( this, env);
            result = Math.pow(val.v(), result);
        }
        return new NumVal(result);
    }

    @Override
    public Value visit(VarExp e, Env env) {
        return env.get(e.name());
    }

    @Override
    public Value visit(LetExp e, Env env) {
        e.getDeclaration().forEach((k, v) -> {});
        for(Map.Entry<String, Exp> entry: e.getDeclaration().entrySet()){
            env = new ExtendEnv(env, entry.getKey(), (Value) entry.getValue().accept(this, env));
        }
        return (Value) e.getBody().accept(this, env);
    }
}
