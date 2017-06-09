/**
 * ģ��ALU���������͸���������������
 * @author 161250220_��Դ
 *
 */

public class ALU {

	/**
	 * ����ʮ���������Ķ����Ʋ����ʾ��<br/>
	 * ����integerRepresentation("9", 8)
	 * @param number ʮ������������Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param length �����Ʋ����ʾ�ĳ���
	 * @return number�Ķ����Ʋ����ʾ������Ϊlength
	 */
	public String integerRepresentation (String number, int length) {
		int num = Integer.valueOf(number);
		StringBuffer numBuffer = new StringBuffer();
		for (int i = 0; i < length; i++)
			numBuffer.append((num & (1 << length - i - 1)) == 0 ? "0" : "1");
		return numBuffer.toString();
	}
	
	/**
	 * ����ʮ���Ƹ������Ķ����Ʊ�ʾ��
	 * ��Ҫ���� 0������񻯡����������+Inf���͡�-Inf������ NaN�����أ������� IEEE 754��
	 * �������Ϊ��0���롣<br/>
	 * ����floatRepresentation("11.375", 8, 11)
	 * @param number ʮ���Ƹ�����������С���㡣��Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return number�Ķ����Ʊ�ʾ������Ϊ 1+eLength+sLength���������ң�����Ϊ���š�ָ���������ʾ����β������λ���أ�
	 */
	public String floatRepresentation (String number, int eLength, int sLength) {
		if (number.equals("+Inf")) {
			return "0" + signExtened("1", eLength) + signExtened("0", sLength);
		} else if (number.equals("-Inf")) {
			return "1" + signExtened("1", eLength) + signExtened("0", sLength);
		}
		String sign = (number.charAt(0) == '-') ? "1" : "0";
		String[] numbers = number.split("\\.");
		if (sign.equals("1")) {
			numbers[0] = numbers[0].substring(1);
		}
		int intNum = Integer.valueOf(numbers[0]);
		numbers[0] = Integer.toBinaryString(intNum);
		if (numbers[0].equals("0")) {
			//�ж��Ƿ�Ϊȫ0
			if (numbers.length == 1 || (numbers[1].length() == 1 && numbers[1].equals("0"))) {
				return sign + signExtened("0", eLength + sLength);
			}
			//�õ���������ֵ
			double trueValue = Double.valueOf("0." + numbers[1]);
			int deviate;
			for (deviate = 0; trueValue < 1 && ((1 << (eLength - 1)) - deviate - 2) > 0; deviate++) {
				trueValue = trueValue * 2;
			}
			if (trueValue >= 1) {
				//��񻯸�����
				trueValue = trueValue - 1;
				StringBuffer pointBuffer = new StringBuffer();
				for (int i = 0; i < sLength; i++) {
					if (trueValue * 2 >= 1) {
						pointBuffer.append('1');
						trueValue = 2 * trueValue - 1;
					} else {
						pointBuffer.append('0');
						trueValue = 2 * trueValue;
					}
				}
				String eString = Integer.toBinaryString((1 << (eLength - 1)) - 1 - deviate);
				String suppleZero = "";
				for (int i = 0; i + eString.length() < eLength; i++)
					suppleZero += "0";
				return sign + suppleZero + eString + pointBuffer.toString();
			} else {
				//�ǹ����
				String eString = signExtened("0", eLength);
				StringBuffer pointBuffer = new StringBuffer();
				for (int i = 0; i < sLength; i++) {
					if (trueValue * 2 >= 1) {
						pointBuffer.append('1');
						trueValue = 2 * trueValue - 1;
					} else {
						pointBuffer.append('0');
						trueValue = 2 * trueValue;
					}
				}
				return sign + eString + pointBuffer.toString();
			}
		} else {
			int max = 1 << (eLength - 1);
			//���ϳ���������Χ,�Ǿͷ���������ʾ
			if (max <= numbers[0].length()) {
				return sign + signExtened("0", eLength + sLength); 
			}
			//��ʼת��С��
			StringBuffer pointBuffer = new StringBuffer();
			double temp = Double.valueOf("0." + numbers[1]);
			for (int i = numbers[0].length() - 1; i < sLength; i++) {
				if (temp * 2 >= 1) {
					pointBuffer.append('1');
					temp = 2 * temp - 1;
				} else {
					pointBuffer.append('0');
					temp = 2 * temp;
				}
			}
			String sString = numbers[0] + pointBuffer.toString();
			String eString = Integer.toBinaryString((numbers[0].length() - 1) + (1 << (eLength - 1)) - 1);
			return sign + eString + sString.substring(1);
		}
	}
	
	/**
	 * ����ʮ���Ƹ�������IEEE 754��ʾ��Ҫ�����{@link #floatRepresentation(String, int, int) floatRepresentation}ʵ�֡�<br/>
	 * ����ieee754("11.375", 32)
	 * @param number ʮ���Ƹ�����������С���㡣��Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 * @param length �����Ʊ�ʾ�ĳ��ȣ�Ϊ32��64
	 * @return number��IEEE 754��ʾ������Ϊlength���������ң�����Ϊ���š�ָ���������ʾ����β������λ���أ�
	 */
	public String ieee754 (String number, int length) {
		if (length == 32) {
			return floatRepresentation(number, 8, 23);
		} else {
			return floatRepresentation(number, 11, 52);
		}
	}
	
	/**
	 * ��������Ʋ����ʾ����������ֵ��<br/>
	 * ����integerTrueValue("00001001")
	 * @param operand �����Ʋ����ʾ�Ĳ�����
	 * @return operand����ֵ����Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ
	 */
	public String integerTrueValue (String operand) {
		int length = operand.length();
		if (operand.charAt(0) == '0') {
			char[] oper = operand.toCharArray();
			int trueValue = 0;
			for (int i = 0; i < length - 1; i++) {
				if (oper[length-1-i] == '1') {
					trueValue += 1 << i;
				}
			}
			return "" + trueValue;
		} else {
			String reverseOper = oneAdder(negation(operand)).substring(1);
			if (reverseOper.charAt(0) == '1') {
				return "-" + (1 << (length - 1));
			}
			char[] oper = reverseOper.toCharArray();
			int trueValue = 0;
			for (int i = 0; i < length - 1; i++) {
				if (oper[length-1-i] == '1') {
					trueValue += (1 << i);
				}
			}
			return (trueValue != 0) ? ("-" + trueValue) : "0";
		}
	}
	
	/**
	 * ���������ԭ���ʾ�ĸ���������ֵ��<br/>
	 * ����floatTrueValue("01000001001101100000", 8, 11)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return operand����ֵ����Ϊ���������һλΪ��-������Ϊ������ 0�����޷���λ����������ֱ��ʾΪ��+Inf���͡�-Inf���� NaN��ʾΪ��NaN��
	 */
	public String floatTrueValue (String operand, int eLength, int sLength) {
		String sign = (operand.charAt(0) == '1') ? "-" : "";
		String eString = operand.substring(1, 1 + eLength);
		String sString = operand.substring(1 + eLength);
		int eNumber = Integer.valueOf(integerTrueValue("0" + eString));
		if (eNumber == 0 && isZero(sString)) {
			return "0.0";
		} else if (eNumber == ((1 << eLength) - 1)) {
			if (isZero(sString)) {
				return ((sign.equals("-")) ? "-" : "+") + "Inf";
			} else {
				return "NaN";
			}
		} else if (eNumber != 0) {
			int power = eNumber - ((1 << (eLength - 1)) - 1);
			if (power > 0) {
				String intPart = "1" + sString.substring(0, power);
				String pointPart = sString.substring(power);
				String part1 = integerTrueValue("0" + intPart);
				double point = 0.0;
				for (int i = 0; i < pointPart.length(); i++) {
					if (pointPart.charAt(i) == '1') {
						point += 1.0 / getN(i + 1);
					}
				}
				String part2 = Double.toString(point).substring(1);
				return sign + part1 + part2;
			} else {
				double point = 0.0;
				for (int i = 0; i < sString.length(); i++) {
					if (sString.charAt(i) == '1') {
						point += 1.0 / getN(i + 1);
					}
				}
				point += 1;
				point = point / getN(-power);
				String part = Double.toString(point);
				return sign + part;
			}
		} else {
			int power = 2 - (1 << (eLength - 1));
			double facter = getN(-power);
			double point = 0.0;
			for (int i = 0; i < sString.length(); i++) {
				if (sString.charAt(i) == '1') {
					point += 1.0 / getN(i + 1);
				}
			}
			double trueValue = point / facter;
			String part = Double.toString(trueValue);
			return sign + part;
		}
	}
	
	/**
	 * ����2��n�η�
	 * @param n
	 * @return
	 */
	private double getN(int n) {
		double operand = 1.0;
		if (n >= 0){
			for (int i = 0; i < n; i++) {
				operand *= 2;
			}
			return operand;
		} else {
			for (int i = 0; i < -n; i++) {
				operand /= 2.0;
			}
			return operand;
		}
	}
	
	/**
	 * ��λȡ��������<br/>
	 * ����negation("00001001")
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @return operand��λȡ���Ľ��
	 */
	public String negation (String operand) {
		char[] originalCharArray = operand.toCharArray();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < operand.length(); i++) 
			stringBuffer.append(originalCharArray[i] == '1' ? "0" : "1");
		return stringBuffer.toString();
	}
	
	/**
	 * ���Ʋ�����<br/>
	 * ����leftShift("00001001", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand����nλ�Ľ��
	 */
	public String leftShift (String operand, int n) {
		if (n >= operand.length()) {
			return signExtened("0", operand.length());
		}
		StringBuffer endStringBuff = new StringBuffer();
		for (int i = 0; i < n; i ++) endStringBuff.append("0");
		String endString = endStringBuff.toString();
		if (n == operand.length()) {
			return endString;
		} else {
			String remain = operand.substring(n);
			return remain + endString;
		}
	}
	
	/**
	 * �߼����Ʋ�����<br/>
	 * ����logRightShift("11110110", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand�߼�����nλ�Ľ��
	 */
	public String logRightShift (String operand, int n) {
		if (n >= operand.length()) {
			return signExtened("0", operand.length());
		}
		StringBuffer startStringBuff = new StringBuffer();
		for (int i = 0; i < n; i++) startStringBuff.append("0");
		String startString = startStringBuff.toString();
		if (n == operand.length()) {
			return startString;
		} else {
			String remain = operand.substring(0, operand.length() - n);
			return startString + remain;
		}
	}
	
	/**
	 * �������Ʋ�����<br/>
	 * ����logRightShift("11110110", 2)
	 * @param operand �����Ʊ�ʾ�Ĳ�����
	 * @param n ���Ƶ�λ��
	 * @return operand��������nλ�Ľ��
	 */
	public String ariRightShift (String operand, int n) {
		if (n > operand.length()) {
			n = operand.length();
		}
		StringBuffer startStringBuff = new StringBuffer();
		for (int i = 0; i < n; i++) startStringBuff.append(operand.substring(0, 1));
		String startString = startStringBuff.toString();
		if (n == operand.length()) {
			return startString;
		} else {
			String remain = operand.substring(0, operand.length() - n);
			return startString + remain;
		}
	}
	
	/**
	 * ȫ����������λ�Լ���λ���мӷ����㡣<br/>
	 * ����fullAdder('1', '1', '0')
	 * @param x ��������ĳһλ��ȡ0��1
	 * @param y ������ĳһλ��ȡ0��1
	 * @param c ��λ�Ե�ǰλ�Ľ�λ��ȡ0��1
	 * @return ��ӵĽ�����ó���Ϊ2���ַ�����ʾ����1λ��ʾ��λ����2λ��ʾ��
	 */
	public String fullAdder (char x, char y, char c) {
		int add1 = (x == '1') ? 1 : 0;
		int add2 = (y == '1') ? 1 : 0;
		int cin = (c == '1') ? 1 : 0;
		int f = add1 ^ add2 ^ cin;
		int cout = cin & (add1 | add2) | (add1 & add2);
		return ((cout == 1) ? "1" : "0") + ((f == 1) ? "1" : "0");
	}
	
	/**
	 * 4λ���н�λ�ӷ�����Ҫ�����{@link #fullAdder(char, char, char) fullAdder}��ʵ��<br/>
	 * ����claAdder("1001", "0001", '1')
	 * @param operand1 4λ�����Ʊ�ʾ�ı�����
	 * @param operand2 4λ�����Ʊ�ʾ�ļ���
	 * @param c ��λ�Ե�ǰλ�Ľ�λ��ȡ0��1
	 * @return ����Ϊ5���ַ�����ʾ�ļ����������е�1λ�����λ��λ����4λ����ӽ�������н�λ��������ѭ�����
	 */
	public String claAdder (String operand1, String operand2, char c) {
		char[] op1 = operand1.toCharArray();
		char[] op2 = operand2.toCharArray();
		char p1 = passOn(op1[3], op2[3]);
		char p2 = passOn(op1[2], op2[2]);
		char p3 = passOn(op1[1], op2[1]);
		char p4 = passOn(op1[0], op2[0]);
		char g1 = generate(op1[3], op2[3]);
		char g2 = generate(op1[2], op2[2]);
		char g3 = generate(op1[1], op2[1]);
		char g4 = generate(op1[0], op2[0]);
		char cin1 = or(g1, and(p1, c));
		char cin2 = or(g2, or(and(p2, g1), and(p2, and(p1, c))));
		char cin3 = or(g3, or(and(p3, g2), or(and(p3, and(p2, g1)), and(and(p3, p2), and(p1, c)))));
		char cin4 = or(g4, or(and(p4, g3), or(and(and(p4, p3), g2), or(and(and(p4, p3), and(g1, p2)), and(and(p4, p3), and(p2, and(p1, c)))))));
		char out1 = fullAdder(op1[3], op2[3], c).charAt(1);
		char out2 = fullAdder(op1[2], op2[2], cin1).charAt(1);
		char out3 = fullAdder(op1[1], op2[1], cin2).charAt(1);
		char out4 = fullAdder(op1[0], op2[0], cin3).charAt(1);
		return new StringBuffer().append(cin4).append(out4).append(out3).append(out2).append(out1).toString();
	}
	
	/**
	 * ��λ���ݺ���
	 * @param x ��һ������
	 * @param y �ڶ�������
	 * @return ��λ���ݺ���ֵ
	 */
	private char passOn(char x, char y) {
		return or(x, y);
	}
	
	/**
	 * ��λ���ɺ���
	 * @param x ��һ������
	 * @param y �ڶ�������
	 * @return ��λ���ɺ���ֵ
	 */
	private char generate(char x, char y) {
		return and(x, y);
	}
	
	/**
	 * �ַ�and
	 * @param a
	 * @param b
	 * @return
	 */
	private char and(char a, char b) {
		if (a == '1' && b == '1') {
			return '1';
		} else {
			return '0';
		}
	}
	
	/**
	 * �ַ���
	 * @param a
	 * @param b
	 * @return
	 */
	private char or(char a, char b) {
		if (a == '0' && b == '0') {
			return '0';
		} else {
			return '1';
		}
	}
	
	/**
	 * ��һ����ʵ�ֲ�������1�����㡣
	 * ��Ҫ�������š����š�����ŵ�ģ�⣬
	 * ������ֱ�ӵ���{@link #fullAdder(char, char, char) fullAdder}��
	 * {@link #claAdder(String, String, char) claAdder}��
	 * {@link #adder(String, String, char, int) adder}��
	 * {@link #integerAddition(String, String, int) integerAddition}������<br/>
	 * ����oneAdder("00001001")
	 * @param operand �����Ʋ����ʾ�Ĳ�����
	 * @return operand��1�Ľ��������Ϊoperand�ĳ��ȼ�1�����е�1λָʾ�Ƿ���������Ϊ1������Ϊ0��������λΪ��ӽ��
	 */
	public String oneAdder (String operand) {
		char[] oper = ("0" + operand).toCharArray();
		int length = oper.length - 1;
		for (int i = length; i > 0; i--) {
			if (oper[i] == '1') {
				oper[i] = '0';
			} else {
				oper[i] = '1';
				if (i == 1) {
					oper[0] = '1';
				}
				break;
			}
		}
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i <= length; i++) {
			stringBuffer.append(oper[i]);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * �ӷ�����Ҫ�����{@link #claAdder(String, String, char)}����ʵ�֡�<br/>
	 * ����adder("0100", "0011", ��0��, 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param c ���λ��λ
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����ӽ��
	 */
	public String adder (String operand1, String operand2, char c, int length) {
		String formalOperand1 = signExtened(operand1, length);
		String formalOperand2 = signExtened(operand2, length);
		StringBuffer stringBuffer = new StringBuffer();
		String[] outcome = new String[length / 4]; //�ֳ�length/4��������4λ���н�λ�ӷ���
		char cout = c;
		for (int i = 0; i < length / 4; i++) {
			String subString1 = formalOperand1.substring(length - 4 * i - 4, length - 4 * i);
			String subString2 = formalOperand2.substring(length - 4 * i - 4, length - 4 * i);
			String tempOutcome = claAdder(subString1, subString2, cout);
			cout = tempOutcome.charAt(0);
			outcome[length / 4 - 1 - i] = tempOutcome.substring(1);
		}
		for (int i = 0; i < length / 4; i++) {
			stringBuffer.append(outcome[i]);
		}
		String valueOutcome = stringBuffer.toString();
		char overFlow = '0';
		if (formalOperand1.charAt(0) == formalOperand2.charAt(0)) {
			if (formalOperand1.charAt(0) != valueOutcome.charAt(0)) {
				overFlow = '1';
			}
		}
		if (overFlow == '1') {
			return "1" + valueOutcome;
		} else {
			return "0" + valueOutcome;
		}
	}
	
	/**
	 * ��һ�����ֽ��з�����չ
	 * @param operand ����
	 * @param length ��չ��λ����
	 * @return
	 */
	public String signExtened(String operand, int length) {
		int originLength = operand.length();
		if (length <= originLength) {
			return operand;
		} else {
			int ext = length - originLength;
			char extChar = '1';
			if (operand.charAt(0) == '0') {
				extChar = '0';
			} 
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < ext; i++) {
				stringBuffer.append(extChar);
			}
			return stringBuffer.toString() + operand;
		}
	}
	
	/**
	 * �����ӷ���Ҫ�����{@link #adder(String, String, char, int) adder}����ʵ�֡�<br/>
	 * ����integerAddition("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����ӽ��
	 */
	public String integerAddition (String operand1, String operand2, int length) {
		return adder(operand1, operand2, '0', length);
	}
	
	/**
	 * �����������ɵ���{@link #adder(String, String, char, int) adder}����ʵ�֡�<br/>
	 * ����integerSubtraction("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ļ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ��������
	 */
	public String integerSubtraction (String operand1, String operand2, int length) {
		return adder(operand1, negation(operand2), '1', length);
	}
	
	/**
	 * �����˷���ʹ��Booth�㷨ʵ�֣��ɵ���{@link #adder(String, String, char, int) adder}�ȷ�����<br/>
	 * ����integerMultiplication("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ĳ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊlength+1���ַ�����ʾ����˽�������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������lengthλ����˽��
	 */
	public String integerMultiplication (String operand1, String operand2, int length) {
		String normalOperand1 = signExtened(operand1, length);
		String normalOperand2 = signExtened(operand2, length);
		String register = signExtened(normalOperand2, 2 * length) + "0";
		for (int i = 0; i < length; i++) {
			if (register.substring(register.length() - 2).equals("10")) {
				String pRegister = register.substring(0, length);
				String tempResult = adder(pRegister, negation(normalOperand1), '1', length).substring(1);
				register = tempResult + register.substring(length);
			} else if (register.substring(register.length() - 2).equals("01")) {
				String pRegister = register.substring(0, length);
				String tempResult = adder(pRegister, normalOperand1, '0', length).substring(1);
				register = tempResult + register.substring(length);
			} 
			register = ariRightShift(register, 1);
		}
		String result;
		if (register.substring(0, length + 1).equals(signExtened("0000", length + 1))
				|| register.substring(0, length + 1).equals(signExtened("1111", length + 1))) {
			result = "0" + register.substring(length, 2 * length);
		} else {
			result =  "1" + register.substring(length, 2 * length);
		}
		if (result.charAt(1) == '1') {
			if (result.charAt(result.length() - 1) == '0') {
				return result.substring(result.length() - 1) + "1";
			}
			for (int i = result.length() - 1; i >= 2; i--) {
				if (result.charAt(i) == '1') {
					continue;
				} else {
					return result.substring(0, i) + "1" + signExtened("0", result.length() - 1 - i);
				}
			}
		}
		return result;
	}
	
	/**
	 * �����Ĳ��ָ������������ɵ���{@link #adder(String, String, char, int) adder}�ȷ���ʵ�֡�<br/>
	 * ����integerDivision("0100", "0011", 8)
	 * @param operand1 �����Ʋ����ʾ�ı�����
	 * @param operand2 �����Ʋ����ʾ�ĳ���
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ���ĳ���������ĳ���С��lengthʱ����Ҫ�ڸ�λ������λ
	 * @return ����Ϊ2*length+1���ַ�����ʾ�������������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0�������lengthλΪ�̣����lengthλΪ����
	 */
	public String integerDivision (String operand1, String operand2, int length) {
		if (isZero(operand2)) {
			return "NaN";
		}
		if (isZero(operand1)) {
			return signExtened("0", 2 * length + 1);
		} 
		String normalOperand1 = signExtened(operand1, 2 * length);
		String normalOperand2 = signExtened(operand2, length);
		String rRegister = normalOperand1.substring(0, length);
		String qRegister = normalOperand1.substring(length);
		String R = rRegister;
		char[] Q = new char[length + 1];
		char Qn;
		for (int i = 0; i < length + 1; i++) {
			if (R.charAt(0) == operand2.charAt(0)) {
				R = integerSubtraction(R, normalOperand2, length).substring(1);
			} else {
				R = integerAddition(R, normalOperand2, length).substring(1);
			}
			if (R.charAt(0) == operand2.charAt(0)) {
				Q[i] = '1';
			} else {
				Q[i] = '0';
			}
			if (i < length) {
					R = R.substring(1) + qRegister.charAt(i);
			}
		}
		//������
		String qString = null;
		String rString = null;
		if (operand1.charAt(0) == operand2.charAt(0)) {
			qString = new String(Q).substring(1);
		} else {
			qString = oneAdder(new String(Q).substring(1)).substring(1);
		}
		//��������
		if (R.charAt(0) == operand1.charAt(0)) {
			rString = R;
		} else {
			if (operand1.charAt(0) == operand2.charAt(0)) {
				rString = integerAddition(R, normalOperand2, length).substring(1);
			} else {
				rString = integerSubtraction(R, normalOperand2, length).substring(1);
			}
		}
		//ȷ���Ƿ����
		if (operand1.charAt(0) == operand2.charAt(0) && Q[0] == '1') {
			Qn = '1';
		} else if (operand1.charAt(0) != operand2.charAt(0) && Q[0] == '0') {
			Qn = '1';
		} else {
			Qn = '0';
		}
		return Qn + qString + rString;
	}

	
	/**
	 * �ж�һ�����Ƿ�ȫ��λΪ��
	 * @param num һ�������ƴ�
	 * @return
	 */
	private boolean isZero(String num) {
		for (int i = 0; i < num.length(); i++) {
			if (num.charAt(i) == '1') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * �����������ӷ������Ե���{@link #adder(String, String, char, int) adder}�ȷ�����
	 * ������ֱ�ӽ�������ת��Ϊ�����ʹ��{@link #integerAddition(String, String, int) integerAddition}��
	 * {@link #integerSubtraction(String, String, int) integerSubtraction}��ʵ�֡�<br/>
	 * ����signedAddition("1100", "1011", 8)
	 * @param operand1 ������ԭ���ʾ�ı����������е�1λΪ����λ
	 * @param operand2 ������ԭ���ʾ�ļ��������е�1λΪ����λ
	 * @param length ��Ų������ļĴ����ĳ��ȣ�Ϊ4�ı�����length��С�ڲ������ĳ��ȣ����������ţ�����ĳ���������ĳ���С��lengthʱ����Ҫ���䳤����չ��length
	 * @return ����Ϊlength+2���ַ�����ʾ�ļ����������е�1λָʾ�Ƿ���������Ϊ1������Ϊ0������2λΪ����λ����lengthλ����ӽ��
	 */
	public String signedAddition (String operand1, String operand2, int length) {
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		if (operand1.length() < length) {
			operand1 = operand1.charAt(0) + signExtened("0", length - operand1.length()) + operand1.substring(1);
		}
		if (operand2.length() < length) {
			operand2 = operand2.charAt(0) + signExtened("0", length - operand2.length()) + operand2.substring(1);
		}
		String newOperand1 = operand1.substring(1);
		String newOperand2 = operand2.substring(1);
		if (sign1 == sign2) {
			//ͬ�����
			return adderHelp(newOperand1, newOperand2, '0', length).substring(0, 1) + sign1 + adderHelp(newOperand1, newOperand2, '0', length).substring(1);
		} else {
			String tempResult = adderHelp(newOperand1, negation(newOperand2), '1', length);
			if (tempResult.charAt(0) == '1') {
				return "0" + sign1 + tempResult.substring(1);
			} else {
				return "1" + sign2 + negation(tempResult.substring(1));
			}
		}
	}
	
	public String adderHelp (String operand1, String operand2, char c, int length) {
		String formalOperand1 = signExtened(operand1, length);
		String formalOperand2 = signExtened(operand2, length);
		StringBuffer stringBuffer = new StringBuffer();
		String[] outcome = new String[length / 4]; //�ֳ�length/4��������4λ���н�λ�ӷ���
		char cout = c;
		for (int i = 0; i < length / 4; i++) {
			String subString1 = formalOperand1.substring(length - 4 * i - 4, length - 4 * i);
			String subString2 = formalOperand2.substring(length - 4 * i - 4, length - 4 * i);
			String tempOutcome = claAdder(subString1, subString2, cout);
			cout = tempOutcome.charAt(0);
			outcome[length / 4 - 1 - i] = tempOutcome.substring(1);
		}
		for (int i = 0; i < length / 4; i++) {
			stringBuffer.append(outcome[i]);
		}
		String valueOutcome = stringBuffer.toString();
		return cout + valueOutcome;
	}
	
	/**
	 * ���н�λ�ӷ���
	 * ר��Ϊ�ӵ��ĸ������ӷ������
	 * @param operand1 ��һ��������
	 * @param operand2 �ڶ���������
	 * @param length ���ý����Ҫ�󳤶ȣ�Ҳ�������������ĳ��ȣ�
	 * @return ����һ������Ϊ1+length���ַ�������һλָʾ�Ƿ������1��ʾ�����0��ʾû���������lengthλ�Ǽӷ�������
	 */
	private String serialAdder(String operand1, String operand2, int length) {
		char c = '0';
		char[] f = new char[length];
		String temp = null;
		for (int i = length - 1; i >= 0; i--) {
			temp = fullAdder(operand1.charAt(i), operand2.charAt(i), c);
			c = temp.charAt(0);
			f[i] = temp.charAt(1);
		}
		return c + new String(f);
	}
	
	/**
	 * ���ڿ��ǲ��ܵ������Ա������һ���н�λ�Ĵ��н�λ�ӷ���
	 * Ϊ�˿ӵ��ĸ��������������
	 * @param operand1 ��һ����������
	 * @param operand2 �ڶ���������
	 * @param length ���ý��Ҫ�󳤶�
	 * @param cin  ���λ��λ
	 * @return ����һ��1+length���ַ�������һλָʾ�Ƿ������1��ʾ�����0��ʾû���������lengthλ��������
	 */
	private String serialAdderAmend(String operand1, String operand2, int length, char cin) {
		char c = cin;
		char[] f = new char[length];
		String temp = null;
		for (int i = length - 1; i >= 0; i--) {
			temp = fullAdder(operand1.charAt(i), operand2.charAt(i), c);
			c = temp.charAt(0);
			f[i] = temp.charAt(1);
		}
		return c + new String(f);
	}
	
	/**
	 * ���н�λ������
	 * ר��Ϊ�ӵ��ĸ��������������
	 * ����Ƹ�����������ô��ô�鷳���Σ�
	 * @param operand1
	 * @param operand2
	 * @return
	 */
	private String serialSubtraction(String operand1, String operand2) {
		return serialAdderAmend(operand1, negation(operand2), operand1.length(), '1');
	}
	
	/**
	 * ���ڼ����ַ����Ľ�λ
	 * @param original Ҫ����λ�Ĳ�����
	 * @return �����ַ�������Ϊoriginal��λ��Ĳ���
	 */
	private String carryBit(String original) {
		char[] bits = original.toCharArray();
		for (int i = original.length() - 1; i >= 0; i--) {
			if (bits[i] == '1') {
				bits[i] = '0';
			} else {
				bits[i] = '1';
				break;
			}
		}
		return new String(bits);
	}
	
	/**
	 * ���뵽ż��
	 * @param original Ҫ������Ĳ�����
	 * @return ����ֵ��carryBit�ṹ��ͬ
	 */
	private String roundToEven(String original) {
		if (original.endsWith("0")) {
			return original;
		} else {
			return carryBit(original);
		}
	}
	
	/**
	 * �������ӷ����ɵ���{@link #signedAddition(String, String, int) signedAddition}�ȷ���ʵ�֡�<br/>
	 * �Ӽ���ͬ�壬�š�
	 * ���������������ͬ�����ӷ���������������
	 * ����floatAddition("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ļ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param gLength ����λ�ĳ���
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����ӽ�������е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatAddition (String operand1, String operand2, int eLength, int sLength, int gLength) {
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		char finalSign = sign1;
		String exponent1 = operand1.substring(1, 1 + eLength);
		String exponent2 = operand2.substring(1, 1 + eLength);
		String finalExp = null;
		String finalEnd = null;
		String end1 = operand1.substring(1 + eLength);
		String end2 = operand2.substring(1 + eLength);
		//�������
		int delta = Integer.valueOf(integerTrueValue(exponent1)) - Integer.valueOf(integerTrueValue(exponent2));
		if (delta > 0) {
			finalExp = exponent1;
		} else {
			finalExp = exponent2;
		}
		if (!exponent1.equals(signExtened("0", gLength)) && !exponent2.equals(signExtened("0", gLength))) { //������ǹ������
			if (sign1 == sign2) {  //���������ͬ�ŵ��������ӷ�
				finalSign = sign1;
				String extendEnd1 = "1" + end1 + signExtened("0", gLength);
				String extendEnd2 = "1" + end2 + signExtened("0", gLength);
				if (delta >= 0) {
					//��һ�����ֵĽױȽϴ� ֻ��Ҫ�������������OK
					if (delta >= 1 + sLength + gLength) { //���߽��������
						return "0" + operand1;
					} else { //�����಻���ر�󣬽�������
						//��ȡ��λ�Ժ�ĵڶ����������ֵ�β������
						if (delta > 0) {
							extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
						} //�������0����Ҫ������λ��ֱ�����½��в����Ϳ�����
						String tempResult = serialAdder(extendEnd1, extendEnd2, extendEnd1.length());
						if (tempResult.charAt(0) == '0') {
							if (tempResult.charAt(2 + sLength) == '0') {
								finalEnd = tempResult.substring(2, 2 + sLength);
							} else {
								for (int i = 3 + sLength; i < tempResult.length(); i++) {
									if (tempResult.charAt(i) == '1') {
										tempResult = carryBit(tempResult.substring(0, 2 + sLength));
										break;
									} 
									if (i == tempResult.length() - 1) {
										tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
									}
								}
								if (tempResult.charAt(0) == '0') { //�������Ҫ��λ
									finalEnd = tempResult.substring(2);
								} else { //������Ϊ10.xx����Ҫ�������Ƿ�����
									if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //��������
										return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
									} else { //����û�������
										finalExp = oneAdder(finalExp).substring(1);
										finalEnd = tempResult.substring(1, 1 + sLength);
									}
								}
							}
						} else { //10.xxx
							if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //��������
								return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
							} else { //����û�������
								finalExp = oneAdder(finalExp).substring(1);
								//��������
								if (tempResult.charAt(1 + sLength) == '0') {
									finalEnd = tempResult.substring(1, 1 + sLength);
								} else {
									for (int i = 2 + sLength; i < tempResult.length(); i++) {
										if (tempResult.charAt(i) == '1') {
											tempResult = carryBit(tempResult.substring(0, 1 + sLength));
											break;
										}
										if (i == tempResult.length() - 1) {
											tempResult = roundToEven(tempResult.substring(0, 1 + sLength));
										}
									}
									finalEnd = tempResult.substring(1, 1 + sLength);
								}
							}
						}
						
						return "0" + finalSign + finalExp + finalEnd;
					}
				} else {
					//����͸ı��������˳�� ����Ҫ���ģ��ӷ����Խ���
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			} else { //�����������ŵ������Ǿ�������
				//finalSignĬ����sign1�ķ���
				String extendEnd1 = "1" + end1 + signExtened("0", gLength);
				String extendEnd2 = "1" + end2 + signExtened("0", gLength);
				if (delta >= 0) {
					if (delta > 0) { //���ڰѽ׶���
						extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
					}
					String tempResult = serialSubtraction(extendEnd1, extendEnd2);
					if (tempResult.charAt(0) == '1') {
						//����������ľ���������
						tempResult = tempResult.substring(1); //ȥ����־λ
						finalSign = sign1;
						//�ж��Ƿ�Ϊ0�������ȫ0���Ǿ���Ϊ����0
						if (isZero(tempResult)) {
							return "0" + finalSign + signExtened("0", eLength + sLength);
						} else { //�����Ϊ0���Ǿͽ��жԽ�ɶ�ģ���Ҫ���ǶԽ��Ƿ�0���鷳������д
							int count = 0;
							for (int i = 0; i < tempResult.length(); i++) {
								if (tempResult.charAt(0) == '1') {
									break;
								}
								tempResult = tempResult.substring(1) + "0";
								count ++;
							}
							//count��ָָ������Ҫ����λ��
							if (Integer.valueOf(integerTrueValue(finalExp)) > count) {
								//����Ժ���Ȼ�ǹ������
								finalExp = integerSubtraction(finalExp, integerRepresentation(String.valueOf(count), eLength), eLength).substring(1);
								//�Ȳ���0�����β������Ҫ���������жϣ���ʱ��tempResult�ĳ�����2+eLenght+gLength
								tempResult = "0" + tempResult;
								if (tempResult.charAt(2 + sLength) == '0') {
									finalEnd = tempResult.substring(2, 2 + sLength);
								} else {
									for (int i = 3 + sLength; i < tempResult.length(); i++) {
										if (tempResult.charAt(i) == '1') {
											tempResult = carryBit(tempResult.substring(0, 2 + sLength)); 
											//�������� 10.xx
											if (tempResult.charAt(0) == '1') {
												finalExp = oneAdder(finalExp).substring(1);
												finalEnd = tempResult.substring(1, 1 + sLength);
											} else { //����01.xx
												finalEnd = tempResult.substring(2, 2 + sLength);
											}
											break;
										}
										if (i == tempResult.length() - 1) {
											tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
											if (tempResult.charAt(0) == '1') {
												finalExp = oneAdder(finalExp).substring(1);
												finalEnd = tempResult.substring(1, 1 + sLength);
											} else { //����01.xx
												finalEnd = tempResult.substring(2, 2 + sLength);
											}
										}
									}
								}
								return "0" + finalSign + finalExp + finalEnd;
							} else {
								//����Ժ��ǹ������
								int originalExp = Integer.valueOf(integerTrueValue(finalExp));
								int sub = count - originalExp;
								finalExp = signExtened("0", eLength);
								//����ǹ��β�����֣���ʱ��tempResult��ָ��1��ͷ��һ������
								finalEnd = signExtened("0", sub) + tempResult.substring(0, sLength - sub);
								return "0" + finalSign + finalExp + finalEnd;
							}
						}
					} else {
						//���Ǻܹ����൱�ڵڶ������ľ���ֵ���ڵ�һ��������ת������˳��
						return floatAddition(operand2, operand1, eLength, sLength, gLength);
					}
				} else {
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			}	
		} else {
			if (!exponent1.equals(signExtened("0", gLength)) && exponent2.equals(signExtened("0", gLength))) { //�ڶ�������Ϊ�ǹ������
				if (sign1 == sign2) {  //���������ͬ�ŵ��������ӷ�
					//delta�ǵ�һ�����ֵĽ����ȥ�ڶ�������
					String extendEnd1 = "1" + end1 + signExtened("0", gLength);
					String extendEnd2 = "0" + end2 + signExtened("0", gLength);
					if (delta >= sLength + gLength) {
						return "0" + operand1;
					}
					extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
					String tempResult = serialAdder(extendEnd1, extendEnd2, extendEnd1.length());
					if (tempResult.charAt(0) == '0') {
						if (tempResult.charAt(2 + sLength) == '0') {
							finalEnd = tempResult.substring(2, 2 + sLength);
						} else {
							for (int i = 3 + sLength; i < tempResult.length(); i++) {
								if (tempResult.charAt(i) == '1') {
									tempResult = carryBit(tempResult.substring(0, 2 + sLength));
									break;
								} 
								if (i == tempResult.length() - 1) {
									tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
								}
							}
							if (tempResult.charAt(0) == '0') { //�������Ҫ��λ
								finalEnd = tempResult.substring(2);
							} else { //������Ϊ10.xx����Ҫ�������Ƿ�����
								if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //��������
									return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
								} else { //����û�������
									finalExp = oneAdder(finalExp).substring(1);
									finalEnd = tempResult.substring(1, 1 + sLength);
								}
							}
						}
					} else { //10.xxx
						if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //��������
							return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
						} else { //����û�������
							finalExp = oneAdder(finalExp).substring(1);
							//��������
							if (tempResult.charAt(1 + sLength) == '0') {
								finalEnd = tempResult.substring(1, 1 + sLength);
							} else {
								for (int i = 2 + sLength; i < tempResult.length(); i++) {
									if (tempResult.charAt(i) == '1') {
										tempResult = carryBit(tempResult.substring(0, 1 + sLength));
										break;
									}
									if (i == tempResult.length() - 1) {
										tempResult = roundToEven(tempResult.substring(0, 1 + sLength));
									}
								}
								finalEnd = tempResult.substring(1, 1 + sLength);
							}
						}
					}
					return "0" + finalSign + finalExp + finalEnd;
				} else { //�����������ŵ���������������һ���ǹ�����֣��ڶ����Ƿǹ������
					String extendEnd1 = "1" + end1 + signExtened("0", gLength);
					String extendEnd2 = "0" + end2 + signExtened("0", gLength);
					if (delta > sLength + gLength) {
						return "0" + operand1;
					}
					extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
					String tempResult = serialSubtraction(extendEnd1, extendEnd2);
					//����������ľ���������
					tempResult = tempResult.substring(1); //ȥ����־λ
					finalSign = sign1;
					//�ж��Ƿ�Ϊ0�������ȫ0���Ǿ���Ϊ����0
					if (isZero(tempResult)) {
						return "0" + finalSign + signExtened("0", eLength + sLength);
					} else { //�����Ϊ0���Ǿͽ��жԽ�ɶ�ģ���Ҫ���ǶԽ��Ƿ�0���鷳������д
						int count = 0;
						for (int i = 0; i < tempResult.length(); i++) {
							if (tempResult.charAt(0) == '1') {
								break;
							}
							tempResult = tempResult.substring(1) + "0";
							count ++;
						}
						//count��ָָ������Ҫ����λ��
						if (Integer.valueOf(integerTrueValue(finalExp)) > count) {
							//����Ժ���Ȼ�ǹ������
							finalExp = integerSubtraction(finalExp, integerRepresentation(String.valueOf(count), eLength), eLength).substring(1);
							//�Ȳ���0�����β������Ҫ���������жϣ���ʱ��tempResult�ĳ�����2+eLenght+gLength
							tempResult = "0" + tempResult;
							if (tempResult.charAt(2 + sLength) == '0') {
								finalEnd = tempResult.substring(2, 2 + sLength);
							} else {
								for (int i = 3 + sLength; i < tempResult.length(); i++) {
									if (tempResult.charAt(i) == '1') {
										tempResult = carryBit(tempResult.substring(0, 2 + sLength)); 
										//�������� 10.xx
										if (tempResult.charAt(0) == '1') {
											finalExp = oneAdder(finalExp).substring(1);
											finalEnd = tempResult.substring(1, 1 + sLength);
										} else { //����01.xx
											finalEnd = tempResult.substring(2, 2 + sLength);
										}
										break;
									}
									if (i == tempResult.length() - 1) {
										tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
										if (tempResult.charAt(0) == '1') {
											finalExp = oneAdder(finalExp).substring(1);
											finalEnd = tempResult.substring(1, 1 + sLength);
										} else { //����01.xx
											finalEnd = tempResult.substring(2, 2 + sLength);
										}
									}
								}
							}
							return "0" + finalSign + finalExp + finalEnd;
						} else {
							//����Ժ��ǹ������
							int originalExp = Integer.valueOf(integerTrueValue(finalExp));
							int sub = count - originalExp;
							finalExp = signExtened("0", eLength);
							//����ǹ��β�����֣���ʱ��tempResult��ָ��1��ͷ��һ������
							finalEnd = signExtened("0", sub) + tempResult.substring(0, sLength - sub);
							return "0" + finalSign + finalExp + finalEnd;
						}
					}
				}
			} else if (exponent1.equals(signExtened("0", gLength)) && !exponent2.equals(signExtened("0", gLength))) { //��һ��Ϊ�ǹ������
				if (sign1 == sign2) {  //���������ͬ�ŵ��������ӷ�����ʵֻ��Ҫ��������������λ�þͿ�����
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				} else { //�����������ŵ�����������
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			} else { //���Ƿǹ������
				if (sign1 == sign2) {  //���������ͬ�ŵ��������ӷ�
					String tempResult = serialAdder(end1, end2, sLength);
					if (tempResult.charAt(0) == '1') { //�ӷ������Ҳ���ǿ��԰����˷ǹ�񻯵�״̬
						finalExp = signExtened("0", eLength - 1) + "1"; 
					}
					finalEnd = tempResult.substring(1);
					return "0" + finalSign + finalExp + finalEnd;
				} else { //�����������ŵ�����������
					String tempResult = serialSubtraction(end1, end2);
					if (tempResult.charAt(0) == '1') {
						finalEnd = tempResult.substring(1);
						return "0" + finalSign + signExtened("0", eLength) + finalEnd;
					} else {
						return floatAddition(operand2, operand1, eLength, sLength, gLength);
					}
				}
			}
		}
	}
	
	/**
	 * �������������ɵ���{@link #floatAddition(String, String, int, int, int) floatAddition}����ʵ�֡�<br/>
	 * ����floatSubtraction("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ļ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param gLength ����λ�ĳ���
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ�������������е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatSubtraction (String operand1, String operand2, int eLength, int sLength, int gLength) {
		return floatAddition(operand1, ((operand2.charAt(0) == '1') ? '0' : '1') + operand2.substring(1), eLength, sLength, gLength);
	}
	
	/**
	 * ר��Ϊ�鷳�ĸ������˷������
	 * (��������鷳+����)
	 * @param operand1 ��һ�������� 0.xx
	 * @param operand2 �ڶ��������� 0.xx
	 * @param length ���� С�����ĳ���
	 * @return ����0.xx��xx�ĳ���Ϊ2nλ
	 */
	private String Multiplication(String operand1, String operand2, int length) {
		String tempResult = signExtened("0", length) + operand2.substring(1);
		for (int i = 0; i < length; i++) {
			if (tempResult.charAt(2 * length - 1) == '1') {
				tempResult = serialAdder(tempResult.substring(0, length), operand1.substring(1), length) + tempResult.substring(length, 2 * length - 1);
			} else {
				tempResult = "0" + tempResult.substring(0, length) + tempResult.substring(length, 2 * length - 1);
			}
  		}
		return "0" + tempResult;
	}
	
	/**
	 * �������˷����ɵ���{@link #integerMultiplication(String, String, int) integerMultiplication}�ȷ���ʵ�֡�<br/>
	 * ����floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ĳ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����˽��,���е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatMultiplication (String operand1, String operand2, int eLength, int sLength) {
		//�ж��Ƿ�Ϊ0
		if (isZero(operand1.substring(1)) || isZero(operand2.substring(2))) {
			return signExtened("0", 2 + eLength + sLength);
		}
		//�����������
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		char finalChar;
		if (sign1 == sign2) {
			finalChar = '0';
		} else {
			finalChar = '1';
		}
		//ָ����ı�Ҫ��׼������
		String exponent1 = operand1.substring(1, 1 + eLength);
		String exponent2 = operand2.substring(1, 1 + eLength);
		String finalExponent = null;
		int exp1 = Integer.valueOf(integerTrueValue(exponent1)) - (1 << (eLength - 1)) + 1;
		int exp2 = Integer.valueOf(integerTrueValue(exponent2)) - (1 << (eLength - 1)) + 1;
		int finalEXP = exp1 + exp2;
		int maxExp = (1 << eLength) - 1;
		int formMinExp =  - maxExp + 1;
		int informMinExp = formMinExp - sLength;
		//β��
		String end1 = operand1.substring(1 + eLength);
		String end2 = operand2.substring(1 + eLength);
		String finalEnd = null;
		if (exp1 >= formMinExp && exp2 >= formMinExp) { //�����������
			String extendEnd1 = "01" + end1; //����0.1xx * 0.1xx
			exp1++;
			String extendEnd2 = "01" + end2;
			exp2++;
			String tempResult = Multiplication(extendEnd1, extendEnd2, extendEnd1.length() - 1); //0.xx * 0.xx = 0.xxxx
			int count = 1;
			for (int i = 1; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //�����е�0�����
					break;
				}
				count++;
			}
			tempResult = "0" + tempResult;
			//�����2sLength+2����������
			for (int i = tempResult.length(); i < 2 + 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			if (tempResult.charAt(2 + sLength) == '0') {
				finalEnd = tempResult.substring(2, 2 + sLength);
			} else {
				for (int i = 3 + sLength; i < tempResult.length(); i++) {
					if (tempResult.charAt(i) == '1') {
						tempResult = carryBit(tempResult);
						break;
					}
					if (i == tempResult.length() - 1) {
						tempResult = roundToEven(tempResult);
					}
				}
				//�������˽�λ���ж��Ƿ���10.xx
				if (tempResult.charAt(0) == '1') {
					count --;
					finalEnd = tempResult.substring(1, 1 + sLength);
				} else {
					finalEnd = tempResult.substring(2 , 2 + sLength);
				}
			}
			finalEXP = exp1 + exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalChar + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp){
				return "0" + finalChar + signExtened("0", sLength + eLength); //�������
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) {
				//�ǹ������
				finalExponent = signExtened("0", eLength);
				int delta = formMinExp - finalEXP;
				finalEnd = "1" + finalEnd;
				for (int i = 0; i < delta - 1; i++) {
					finalEnd = "0" + finalEnd;
				}
				finalEnd = finalEnd.substring(0, sLength);
				return "0" + finalChar + finalExponent + finalEnd; 
			} else {
				//�������
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalChar + finalExponent + finalEnd;
			}
		} else if (exp1 >= formMinExp && !(exp2 >= formMinExp)) {
			//�ڶ�������Ϊ�ǹ������
			String extendEnd1 = "01" + end1; //����0.1xx * 0.0xx���൱�ڷֱ�������һλ
			exp1++;
			String extendEnd2 = "00" + end2;
			exp2++;
			String tempResult = Multiplication(extendEnd1, extendEnd2, extendEnd1.length() - 1); //0.xx * 0.xx = 0.xxxx
			int count = 1;
			for (int i = 1; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //�����е�0�����
					break;
				}
				count++;
			}
			tempResult = "0" + tempResult;
			//�����2sLength+2����������
			for (int i = tempResult.length(); i < 2 + 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			if (tempResult.charAt(2 + sLength) == '0') {
				finalEnd = tempResult.substring(2, 2 + sLength);
			} else {
				for (int i = 3 + sLength; i < tempResult.length(); i++) {
					if (tempResult.charAt(i) == '1') {
						tempResult = carryBit(tempResult);
						break;
					}
					if (i == tempResult.length() - 1) {
						tempResult = roundToEven(tempResult);
					}
				}
				//�������˽�λ���ж��Ƿ���10.xx
				if (tempResult.charAt(0) == '1') {
					count --;
					finalEnd = tempResult.substring(1, 1 + sLength);
				} else {
					finalEnd = tempResult.substring(2 , 2 + sLength);
				}
			}
			finalEXP = exp1 + exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalChar + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp){
				return "0" + finalChar + signExtened("0", sLength + eLength); //�������
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) {
				//�ǹ������
				finalExponent = signExtened("0", eLength);
				int delta = formMinExp - finalEXP;
				finalEnd = "1" + finalEnd;
				for (int i = 0; i < delta - 1; i++) {
					finalEnd = "0" + finalEnd;
				}
				finalEnd = finalEnd.substring(0, sLength);
				return "0" + finalChar + finalExponent + finalEnd; 
			} else {
				//�������
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalChar + finalExponent + finalEnd;
			}
		} else if (exp2 >= formMinExp && !(exp1 >= formMinExp)) {
			//��һ������Ϊ�ǹ�����֣�ת������˳��������
			return floatMultiplication(operand2, operand1, eLength, sLength);
		} else { //����˵2�����Ƿǹ������
			//����Ȱ���ˣ����ó��ˣ�ֱ�ӷ���0�Ϳ�����
			return "0" + finalChar + signExtened("0", sLength + eLength);
		}
	}
	
	/**
	 * Ϊ�˸������˳��������
	 * ��ĳ������ת���ɸ�����ָ������
	 * @param exponent ָ��ֵ
	 * @param length ������ָ���򳤶�
	 * @return
	 */
	private String toExponent(int exponent, int length) {
		exponent = exponent + (1 << (length - 1)) - 1;
		return integerRepresentation(String.valueOf(exponent), length + 1).substring(1);
	}
	
	/**
	 * ר��Ϊ�������������
	 * ����������Ҳ̫���˰�
	 * @param operand1 ��һ�������� 0.1xx
	 * @param operand2 �ڶ��������� 0.1xx
	 * @param length ����
	 * @return ���, ����ҲΪlength
	 */
	private String division(String operand1, String operand2, int length) {
		String rAndQ = operand1 + signExtened("0", length);
		String neOperand2 = oneAdder(negation(operand2)).substring(1);
		String temp = null;
		temp = rAndQ.substring(0, length);
		String r1 = serialAdder(temp, neOperand2, length).substring(1);
		if (r1.charAt(0) == '0') { //������0�ָ��������൱��ɶ��û��������0˵�������1.xx
			rAndQ = r1 + rAndQ.substring(length, 2 * length - 1) + '1';
		}
		for(int i = 1; i < length; i++) {
			rAndQ = rAndQ.substring(1);
			temp = rAndQ.substring(0, length);
			temp = serialAdder(temp, neOperand2, length).substring(1);
			if (temp.charAt(0) == '0') {
				rAndQ = temp + rAndQ.substring(length) + "1";
			} else {
				rAndQ = rAndQ + "0";
			}
		}
		return rAndQ.substring(length);
	}
	
	/**
	 * �������������ɵ���{@link #integerDivision(String, String, int) integerDivision}�ȷ���ʵ�֡�<br/>
	 * ����floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 �����Ʊ�ʾ�ı�����
	 * @param operand2 �����Ʊ�ʾ�ĳ���
	 * @param eLength ָ���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @param sLength β���ĳ��ȣ�ȡֵ���ڵ��� 4
	 * @return ����Ϊ2+eLength+sLength���ַ�����ʾ����˽��,���е�1λָʾ�Ƿ�ָ�����磨���Ϊ1������Ϊ0��������λ����������Ϊ���š�ָ���������ʾ����β������λ���أ����������Ϊ��0����
	 */
	public String floatDivision (String operand1, String operand2, int eLength, int sLength) {
		String tempResult = floatDivisionHelper(operand1, operand2, eLength, sLength);
		boolean equals = true;
		for (int i = tempResult.length() - 1; i >= 2; i--) {
			if (!(tempResult.charAt(i) == '0')) {
				equals = false;
				break;
			}
		}
		if (equals) {
			return signExtened("0", 2 + eLength + sLength);
		}
		return tempResult;
	}
	
	public String floatDivisionHelper (String operand1, String operand2, int eLength, int sLength) {
		/**
		 * ���ڸ����������Ĺ������޴��Ҿ��ò������κ��м��ɵ����뷽ʽ������ֱ�ӽضϡ�
		 * �������κξ��ȣ�
		 */
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		char finalSign = '1';
		if (sign1 == sign2) {
			finalSign = '0';
		}
		//ָ����ı�Ҫ��׼������
		String exponent1 = operand1.substring(1, 1 + eLength);
		String exponent2 = operand2.substring(1, 1 + eLength);
		String finalExponent = null;
		int exp1 = Integer.valueOf(integerTrueValue(exponent1)) - (1 << (eLength - 1)) + 1;
		int exp2 = Integer.valueOf(integerTrueValue(exponent2)) - (1 << (eLength - 1)) + 1;
		int finalEXP = exp1 + exp2;
		int maxExp = (1 << eLength) - 1;
		int formMinExp =  - maxExp + 1;
		int informMinExp = formMinExp - sLength;
		//β��
		String end1 = operand1.substring(1 + eLength);
		String end2 = operand2.substring(1 + eLength);
		String finalEnd = null;
		//�����³�0����0�������������
		if (operand1.substring(1).equals(signExtened("0", sLength + eLength))) {
			return "0" + finalSign + signExtened("0", sLength + eLength);
		} else if (operand2.substring(1).equals(signExtened("0", sLength + eLength))) {
			return "0" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
		}
		if (exp1 >= formMinExp && exp2 >= formMinExp) { //�������ǹ������
			String extendEnd1 = "01" + end1; //0.1x
			exp1++;
			String extendEnd2 = "01" + end2; //0.1x
			exp2++;
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //һ��2sLength+1λ����0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //ȥ����ǰ�����е�0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //̫С��
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //�ǹ������
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//�������
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else if (exp1 >= formMinExp && !(exp2 >= formMinExp)) { //�ڶ����Ƿǹ������
			String extendEnd1 = "01" + end1; //0.1x
			exp1++;
			String extendEnd2 = "00" + end2; //0.0x
			exp2++;
			for(int i = 1; i < extendEnd2.length(); i++) {
				if (extendEnd2.charAt(i) != '1') {
					exp2--;
				} else {
					extendEnd2 = extendEnd2.substring(i - 1);
					break;
				}
			}
			for (int i = extendEnd2.length(); i < extendEnd1.length(); i++) {
				extendEnd2 = extendEnd2 + "0";
			}
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //һ��2sLength+1λ����0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //ȥ����ǰ�����е�0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //̫С��
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //�ǹ������
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//�������
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else if (!(exp2 >= formMinExp) && exp1 >= formMinExp) { //��һ���Ƿǹ������
			String extendEnd1 = "01" + end1; //0.1x
			exp1++;
			String extendEnd2 = "00" + end2; //0.0x
			exp2++;
			for(int i = 1; i < extendEnd1.length(); i++) {
				if (extendEnd1.charAt(i) != '1') {
					exp1--;
				} else {
					extendEnd1 = extendEnd1.substring(i - 1);
					break;
				}
			}
			for (int i = extendEnd1.length(); i < extendEnd2.length(); i++) {
				extendEnd1 = extendEnd1 + "0";
			}
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //һ��2sLength+1λ����0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //ȥ����ǰ�����е�0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //̫С��
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //�ǹ������
 				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//�������
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else { //�������Ƿǹ������
			String extendEnd1 = "01" + end1; //0.1x
			exp1++;
			String extendEnd2 = "00" + end2; //0.0x
			exp2++;
			for(int i = 1; i < extendEnd2.length(); i++) {
				if (extendEnd2.charAt(i) != '1') {
					exp2--;
				} else {
					extendEnd2 = extendEnd2.substring(i - 1);
					break;
				}
			}
			for (int i = extendEnd2.length(); i < extendEnd1.length(); i++) {
				extendEnd2 = extendEnd2 + "0";
			}
			for(int i = 1; i < extendEnd1.length(); i++) {
				if (extendEnd1.charAt(i) != '1') {
					exp1--;
				} else {
					extendEnd1 = extendEnd1.substring(i - 1);
					break;
				}
			}
			for (int i = extendEnd1.length(); i < extendEnd2.length(); i++) {
				extendEnd1 = extendEnd1 + "0";
			}
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //һ��2sLength+1λ����0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //ȥ����ǰ�����е�0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //���
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //̫С��
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //�ǹ������
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//�������
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		}
	}
}
