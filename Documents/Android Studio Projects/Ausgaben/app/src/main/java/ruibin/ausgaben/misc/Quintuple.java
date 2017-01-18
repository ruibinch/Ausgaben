package ruibin.ausgaben.misc;

public class Quintuple<S, T, U, V, W> {

	private S first;
	private T second;
	private U third;
	private V fourth;
	private W fifth;
	
	public Quintuple(S first, T second, U third, V fourth, W fifth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.fifth = fifth;
	}
	
	public Quintuple() { }
	
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

	public W getFifth() {
		return fifth;
	}
}
