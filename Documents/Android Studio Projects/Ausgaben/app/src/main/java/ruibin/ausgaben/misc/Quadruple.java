package ruibin.ausgaben.misc;

public class Quadruple <S, T, U, V> {

	private S first;
	private T second;
	private U third;
	private V fourth;
	
	public Quadruple(S first, T second, U third, V fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}
	
	public Quadruple() {
		
	}
	
	public S getFirst() {
		return first;
	}
	
	public T getSecond() {
		return second;
	}
	
	public U getThird() {
		return third;
	}
	
	public V getFourth() {
		return fourth;
	}
}
