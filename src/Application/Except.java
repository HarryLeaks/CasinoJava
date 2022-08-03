package Application;

public class Except extends Exception{
	private static final long serialVersionUID = 1L;

	public static void VerifyNumber(String value) {
		if(Integer.parseInt(value) < 0 || Integer.parseInt(value) > 20) {
			throw new IllegalArgumentException("Valor menor ou igual a zero ou maior que 20");
		}else if(value.isEmpty()){
			throw new NullPointerException("Tem de adicionar um numero");
		}else if(value.matches("%[a-zA-Z]%")){
			throw new NumberFormatException("Não pode haver letras");
		}
		return;
	}
	
	public static void VerifyNoMoney(int value) {
		if(value <= 0) {
			throw new IllegalArgumentException("Valor menor ou igual a zero");
		}
	}
}
