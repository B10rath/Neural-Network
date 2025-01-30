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

    Value Add(Value v,String label){
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        children.add(v);
        Value out=new Value(this.data + v.data,children,label,"+");
        return out;
    }

    Value Sub(Value v,String label){
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        children.add(v);
        Value out=new Value(this.data - v.data,children,label,"-");
        return out;
    }

    Value Mul(Value v,String label){
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        children.add(v);
        Value out=new Value(this.data * v.data,children,label,"*");
        return out;
    }

    Value Tanh(String label){
        double a=this.data;
        double res = (Math.exp(a*2)-1)/(Math.exp(a*2)+1);
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        Value out=new Value(res,children,label,"tanh")
        return out;
    }

    Value Pow(double b,String label){
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        children.add(new Value(b, label));
        Value out=new Value(Math.pow(this.data, b),children,label,"^")
        return out;
    }

    void buildTopo(Value v){
        if(visited.contains(v)==false){
            visited.add(v);
            Iterator<Value> it = v.prev.iterator();
            while(it.hasNext()){
                Value child = it.next(); 
                buildTopo(child);
            }
            topo.add(v);
        }
    }

    void backward(){
        switch(this.op){
            case "+": Value v1=this.prev.get(0);
                      Value v2=this.prev.get(1);
                      v1.grad+=1*this.grad;
                      v2.grad+=1*this.grad;
                      break;
            case "*": Value v3=this.prev.get(0);
                      Value v4=this.prev.get(1);
                      v3.grad+=v4.data*this.grad;
                      v4.grad+=v3.data*this.grad;
                      break;
            case "-": Value v5=this.prev.get(0);
                      Value v6=this.prev.get(1);
                      v5.grad+=1*this.grad;
                      v6.grad+=(-1)*this.grad;
                      break;
            case "^": Value v7=this.prev.get(0);
                      Value v8=this.prev.get(1);
                      v7.grad+=v8.data*Math.pow(v7.data, v8.data-1)*this.grad;
                      break;
            case "tanh": Value v9 = this.prev.get(0);
                         v9.grad+=(1-this.data*this.data)*this.grad;
                         break;
            default: break;
        }
    }
    void reverse(){
        buildTopo(this);
        for(int i=topo.size()-1;i>=0;i--){
            topo.get(i).backward();
        }
    }
}
class Neuron{
    
}
class Nn{
    public static void main(String args[]){
        Value v1=new Value(5.0,"a");
        Value v2=new Value(2.0,"b");
        
        System.out.println(v1.label+" = "+v1.data+";;;"+v2.label+" = "+v2.data);
    }
}