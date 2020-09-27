package com.main;

import com.java.view.RiskGameDriver;

public class RiskGameApplication {

	public static void main(String[] args) {
		System.out.println("\n################ Game Begins ################\n");
		RiskGameDriver driver = new RiskGameDriver();
		driver.startGame();
		System.out.println("\n################ Game Ends ################\n");
    }
}
