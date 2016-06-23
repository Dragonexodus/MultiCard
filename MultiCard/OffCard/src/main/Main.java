package main;

import opencard.core.service.SmartCard;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starte SmartCard");
		try {
			SmartCard.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Beende SmartCard");
		try {
			SmartCard.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
