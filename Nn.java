import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
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
        DecimalFormat df = new DecimalFormat("##.####"); 
        return "" + df.format(data);
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
        Value out=new Value(res,children,label,"tanh");
        return out;
    }

    Value Pow(double b,String label){
        ArrayList<Value> children=new ArrayList<>();
        children.add(this);
        children.add(new Value(b, label));
        Value out=new Value(Math.pow(this.data, b),children,label,"^");
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
    ArrayList<Value> w=new ArrayList<>();
    Random random = new Random();
    Value b;

    Neuron(int n) {
        for (int i = 0; i < n; i++) {
            double wts=(random.nextDouble() * 2) - 1;
            this.w.add(new Value(wts,null));
        }
        double bias=(random.nextDouble() * 2) - 1;
        this.b = new Value(bias, null);
    }

    Value Call(ArrayList<Value> x){
        Value wixi,out;
        Value act = new Value(0, null);
        for (int i = 0; i < this.w.size(); i++) {
            wixi=this.w.get(i).Mul(x.get(i),null);
            act = act.Add(wixi, null);
        }
        act= act.Add(this.b, null);
        out = act.Tanh("y");
        return out;
    }

    ArrayList<Value> Parameters(){
        ArrayList<Value> p=new ArrayList<>();
        p.addAll(this.w);
        p.add(this.b);
        return p;
    } 
}

class Layer{
    ArrayList<Neuron> neurons=new ArrayList<>();

    Layer(int nin,int nout) {
        for (int i = 0; i < nout; i++) {
            this.neurons.add(new Neuron(nin));
        }
    }

    ArrayList<Value> Call(ArrayList<Value> x){
        ArrayList<Value> outs=new ArrayList<>();
        for(Neuron neuron:this.neurons){
            outs.add(neuron.Call(x));
        }
        return outs;
    }

    ArrayList<Value> Parameters(){
        ArrayList<Value>par= new ArrayList<>();
        for (Neuron neuron : this.neurons) {
            par.addAll(neuron.Parameters());
        }
        return par;

    }
}

class MLP{
    ArrayList<Layer> layers = new ArrayList<>();
    MLP(int nin,int[] nouts){
        for (int i = 0; i < nouts.length; i++) {
            this.layers.add(new Layer(nin, nouts[i]));
            nin=nouts[i];
        }
    }

    ArrayList<Value> Call(ArrayList<Value> x){
        for (Layer layer : this.layers) {
                x=layer.Call(x);       
        }
        return x;
    }

    ArrayList<Value> Parameters(){
        ArrayList<Value>params= new ArrayList<>();
        for (Layer layer : this.layers) {
            params.addAll(layer.Parameters());
        }
        return params;
    }

}
class Nn{
    public static void main(String args[]){
        Scanner sc=new Scanner(System.in);
        ArrayList<ArrayList<Value>> y=new ArrayList<>();
        ArrayList<ArrayList<Value>> xall = new ArrayList<>();
        ArrayList<Value> ypred = new ArrayList<>();
        ArrayList<Value> yact = new ArrayList<>();
        Value loss=new Value(0, null);
        int d, n, cnt=0;

        yact.add(new Value(1, null));
        yact.add(new Value(-1, null));
        yact.add(new Value(-1, null));
        yact.add(new Value(1, null));

        ArrayList<Value> x = new ArrayList<>();
        x.add(new Value(2.0, "x1"));
        x.add(new Value(3.0, "x2"));
        x.add(new Value(-1, "x3"));
        xall.add(x);
        x = new ArrayList<>();
        x.add(new Value(3.0, "x1"));
        x.add(new Value(-1.0, "x2"));
        x.add(new Value(0.5, "x3"));
        xall.add(x);
        x = new ArrayList<>();
        x.add(new Value(0.5, "x1"));
        x.add(new Value(1.0, "x2"));
        x.add(new Value(1, "x3"));
        xall.add(x);
        x = new ArrayList<>();
        x.add(new Value(1.0, "x1"));
        x.add(new Value(1.0, "x2"));
        x.add(new Value(-1, "x3"));
        xall.add(x);

        int nouts[]={4,4,1};
        MLP m=new MLP(3,nouts);
        
        ArrayList<Value> params;
        int resume=0;
        do { 
            for(ArrayList<Value> xi:xall){
                y.add(m.Call(xi));
            }
            for(ArrayList<Value> yi:y){
                ypred.add(yi.get(0));
            }
            ++cnt;
            System.out.println("Epochs:"+cnt);
            System.out.println("\tPredicted_Values -> "+ypred);
            System.out.println("\tTarget_Values -> "+yact);

            for (int i = 0; i < ypred.size(); i++) {
                loss=loss.Add((ypred.get(i).Sub(yact.get(i), null).Pow(2, null)),null);
            }

            loss.grad=1;
            loss.reverse();
            System.out.println("\tLoss : "+loss);
            params=m.Parameters();
            for(Value v:params){
                v.data+= -0.01*v.grad;
            }
            for (Value v : params) {
                v.grad=0.0;
            }
            ypred = new ArrayList<>();
            y = new ArrayList<>();
            loss=new Value(0, null);
            resume=sc.nextInt();
        } while (resume==1);

        sc.close();
    }
}