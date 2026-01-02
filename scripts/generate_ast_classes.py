import sys
from pathlib import Path

if len(sys.argv) != 2:
    print("Usage: python3 generate.py <dir_path>")
    exit()

dir_path = sys.argv[1]


## Generates files with Visitor boilerplate
def define_ast(BASE,types):
    file_name = dir_path+"/"+BASE+".java"
    with open(file_name,"w") as f:
        print("package lite;",file=f)
        print("",file=f)
        print("abstract class "+BASE+"{",file=f)
        print("",file=f)
        print("\tabstract <T> T accept(Visitor<T> visitor);",file=f)
        print("",file=f)
        print("\tinterface Visitor<T> {",file=f)
        for type,attributes in types.items():
            print("\t\tT visit"+type+BASE+"("+type+" "+BASE.lower()+");",file=f)
        print("\t}",file=f)
        print("",file=f)
        for type,attributes in types.items():
            print("\tstatic class "+type+" extends "+BASE+" {",file=f)
            constructor_args = ", ".join(attributes)
            print("\t\t"+type+"("+constructor_args+"){",file=f)
            for attribute in attributes:
                attribute_name = attribute.split(" ")[1]
                print("\t\t\t"+"this."+attribute_name+"="+attribute_name+";",file=f)
            print("\t\t}",file=f)
            for attribute in attributes:
                print("\t\tfinal "+attribute+";",file=f)
            print("\t\t@Override",file=f)
            print("\t\t<T> T accept(Visitor<T> visitor){",file=f)
            print("\t\t\treturn visitor.visit"+type+BASE+"(this);",file=f)
            print("\t\t}",file=f)
            print("\t}",file=f)
                
        print("}",file=f)


if __name__=="__main__":
    define_ast("Stmt", {
        "Expression":["Expr expression"],
        "Print":["Expr expression"],
    })
    define_ast("Expr", {
        "Binary"   : ["Expr left", "Token operator", "Expr right"],
        "Unary"    : ["Token operator", "Expr right"],
        "Grouping" : ["Expr expression"],
        "Literal"  : ["Object value"]
    })



