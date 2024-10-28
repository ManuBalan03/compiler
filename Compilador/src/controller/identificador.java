package controller;

public class identificador {

  String Name;
  String Type;
  String Value;
  public identificador(String Name, String Type, String value){
    this.Name=Name;
    this.Type=Type;
    this.Value=value;
  }
  public String getNombre() {
    return Name;
}

public void setNombre(String Name) {
    this.Name = Name;
}

public String getTipo() {
    return Type;
}

public void setTipo(String Type) {
    this.Type = Type;
}

public String getValor() {
    return Value;
}

public void setValor(String Value) {
    this.Value = Value;
}

}