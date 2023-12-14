package application;

public class CaesarCipher {

	public CaesarCipher() {
		// TODO Auto-generated constructor stub
	}

	public String cipherEncrypt(String plainText, int shift) {

		StringBuilder cipherText = new StringBuilder();

		for (char c : plainText.toCharArray()) {
			if (Character.isLetter(c)) {
				char shiftedChar = shiftCharacter(c, shift);
				cipherText.append(shiftedChar);
			} else {
				cipherText.append(c);
			}
		}
		return cipherText.toString();
	}

	public String cipherDecrypt(String cipherText, int shift) {
		return cipherEncrypt(cipherText, -shift);
	}

	private char shiftCharacter(char character, int shift) {
		char base = Character.isUpperCase(character) ? 'A' : 'a';
		int position = character - base;
		int newPosition = (position + shift + 26) % 26;
		return (char) (base + newPosition);
	}

}
