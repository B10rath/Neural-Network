import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
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
        this.label=label;
    }
    Value(double data,ArrayList<Value> children,String label,String op){
        this.data=data;
        this.prev=children;
        this.label=label;
        this.op=op;
    }
    @Override
    public String toString(){
        return "Value(data) : "+data;
    }
    Value Add(Value v){
         return new Value(this.data+v.data);
    }
}
class Nn{
    public static void main(String args[]){
        Value v1=new Value(5.0,"a");
        Value v2=new Value(2.0,"b");
        
        System.out.println(v1.label+" = "+v1.data+";;;"+v2.label+" = "+v2.data);
    }
}