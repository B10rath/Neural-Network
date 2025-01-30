import java.io.*;
import java.util.*;
import java.lang.Math;
import java.sql.Struct;
class Value{
    double data;
    double grad;
    String label="";
    String op="";
    ArrayList<Value> topo=new ArrayList<>();
    ArrayList<Value> prev=new ArrayList<>();
    Set<Value> visited=new HashSet<>();
    Value(double data,String label){
        this.data=data;
        thus.label=label;
    }
    Value(double data,String label,)
    @override
    public String toString(){
        return "Value(data) : "+data
    }
    Value Add(Value v){
        return new Value(this.data+v.data)
    }
}
class Nn{
    public static void main(String args[])
    {
        Value v1=new Value(5);
        Value v2=new Value(0);
        Value v3=v1.div(v2);
        System.out.println(v3);
    }
}