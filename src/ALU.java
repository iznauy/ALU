/**
 * 模拟ALU进行整数和浮点数的四则运算
 * @author 161250220_訾源
 *
 */

public class ALU {

	/**
	 * 生成十进制整数的二进制补码表示。<br/>
	 * 例：integerRepresentation("9", 8)
	 * @param number 十进制整数。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制补码表示的长度
	 * @return number的二进制补码表示，长度为length
	 */
	public String integerRepresentation (String number, int length) {
		int num = Integer.valueOf(number);
		StringBuffer numBuffer = new StringBuffer();
		for (int i = 0; i < length; i++)
			numBuffer.append((num & (1 << length - i - 1)) == 0 ? "0" : "1");
		return numBuffer.toString();
	}
	
	/**
	 * 生成十进制浮点数的二进制表示。
	 * 需要考虑 0、反规格化、正负无穷（“+Inf”和“-Inf”）、 NaN等因素，具体借鉴 IEEE 754。
	 * 舍入策略为向0舍入。<br/>
	 * 例：floatRepresentation("11.375", 8, 11)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return number的二进制表示，长度为 1+eLength+sLength。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
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
			//判断是否为全0
			if (numbers.length == 1 || (numbers[1].length() == 1 && numbers[1].equals("0"))) {
				return sign + signExtened("0", eLength + sLength);
			}
			//得到浮点数的值
			double trueValue = Double.valueOf("0." + numbers[1]);
			int deviate;
			for (deviate = 0; trueValue < 1 && ((1 << (eLength - 1)) - deviate - 2) > 0; deviate++) {
				trueValue = trueValue * 2;
			}
			if (trueValue >= 1) {
				//规格化浮点数
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
				//非规格化数
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
			//向上超出表数范围,那就返回无穷大表示
			if (max <= numbers[0].length()) {
				return sign + signExtened("0", eLength + sLength); 
			}
			//开始转换小数
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
	 * 生成十进制浮点数的IEEE 754表示，要求调用{@link #floatRepresentation(String, int, int) floatRepresentation}实现。<br/>
	 * 例：ieee754("11.375", 32)
	 * @param number 十进制浮点数，包含小数点。若为负数；则第一位为“-”；若为正数或 0，则无符号位
	 * @param length 二进制表示的长度，为32或64
	 * @return number的IEEE 754表示，长度为length。从左向右，依次为符号、指数（移码表示）、尾数（首位隐藏）
	 */
	public String ieee754 (String number, int length) {
		if (length == 32) {
			return floatRepresentation(number, 8, 23);
		} else {
			return floatRepresentation(number, 11, 52);
		}
	}
	
	/**
	 * 计算二进制补码表示的整数的真值。<br/>
	 * 例：integerTrueValue("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位
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
	 * 计算二进制原码表示的浮点数的真值。<br/>
	 * 例：floatTrueValue("01000001001101100000", 8, 11)
	 * @param operand 二进制表示的操作数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return operand的真值。若为负数；则第一位为“-”；若为正数或 0，则无符号位。正负无穷分别表示为“+Inf”和“-Inf”， NaN表示为“NaN”
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
	 * 返回2的n次方
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
	 * 按位取反操作。<br/>
	 * 例：negation("00001001")
	 * @param operand 二进制表示的操作数
	 * @return operand按位取反的结果
	 */
	public String negation (String operand) {
		char[] originalCharArray = operand.toCharArray();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < operand.length(); i++) 
			stringBuffer.append(originalCharArray[i] == '1' ? "0" : "1");
		return stringBuffer.toString();
	}
	
	/**
	 * 左移操作。<br/>
	 * 例：leftShift("00001001", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 左移的位数
	 * @return operand左移n位的结果
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
	 * 逻辑右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand逻辑右移n位的结果
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
	 * 算术右移操作。<br/>
	 * 例：logRightShift("11110110", 2)
	 * @param operand 二进制表示的操作数
	 * @param n 右移的位数
	 * @return operand算术右移n位的结果
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
	 * 全加器，对两位以及进位进行加法运算。<br/>
	 * 例：fullAdder('1', '1', '0')
	 * @param x 被加数的某一位，取0或1
	 * @param y 加数的某一位，取0或1
	 * @param c 低位对当前位的进位，取0或1
	 * @return 相加的结果，用长度为2的字符串表示，第1位表示进位，第2位表示和
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
	 * 4位先行进位加法器。要求采用{@link #fullAdder(char, char, char) fullAdder}来实现<br/>
	 * 例：claAdder("1001", "0001", '1')
	 * @param operand1 4位二进制表示的被加数
	 * @param operand2 4位二进制表示的加数
	 * @param c 低位对当前位的进位，取0或1
	 * @return 长度为5的字符串表示的计算结果，其中第1位是最高位进位，后4位是相加结果，其中进位不可以由循环获得
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
	 * 进位传递函数
	 * @param x 第一个加数
	 * @param y 第二个加数
	 * @return 进位传递函数值
	 */
	private char passOn(char x, char y) {
		return or(x, y);
	}
	
	/**
	 * 进位生成函数
	 * @param x 第一个加数
	 * @param y 第二个加数
	 * @return 进位生成函数值
	 */
	private char generate(char x, char y) {
		return and(x, y);
	}
	
	/**
	 * 字符and
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
	 * 字符或
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
	 * 加一器，实现操作数加1的运算。
	 * 需要采用与门、或门、异或门等模拟，
	 * 不可以直接调用{@link #fullAdder(char, char, char) fullAdder}、
	 * {@link #claAdder(String, String, char) claAdder}、
	 * {@link #adder(String, String, char, int) adder}、
	 * {@link #integerAddition(String, String, int) integerAddition}方法。<br/>
	 * 例：oneAdder("00001001")
	 * @param operand 二进制补码表示的操作数
	 * @return operand加1的结果，长度为operand的长度加1，其中第1位指示是否溢出（溢出为1，否则为0），其余位为相加结果
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
	 * 加法器，要求调用{@link #claAdder(String, String, char)}方法实现。<br/>
	 * 例：adder("0100", "0011", ‘0’, 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param c 最低位进位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String adder (String operand1, String operand2, char c, int length) {
		String formalOperand1 = signExtened(operand1, length);
		String formalOperand2 = signExtened(operand2, length);
		StringBuffer stringBuffer = new StringBuffer();
		String[] outcome = new String[length / 4]; //分成length/4段来调用4位先行进位加法器
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
	 * 把一个数字进行符号扩展
	 * @param operand 数字
	 * @param length 扩展后位长度
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
	 * 整数加法，要求调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerAddition("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被加数
	 * @param operand2 二进制补码表示的加数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相加结果
	 */
	public String integerAddition (String operand1, String operand2, int length) {
		return adder(operand1, operand2, '0', length);
	}
	
	/**
	 * 整数减法，可调用{@link #adder(String, String, char, int) adder}方法实现。<br/>
	 * 例：integerSubtraction("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被减数
	 * @param operand2 二进制补码表示的减数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相减结果
	 */
	public String integerSubtraction (String operand1, String operand2, int length) {
		return adder(operand1, negation(operand2), '1', length);
	}
	
	/**
	 * 整数乘法，使用Booth算法实现，可调用{@link #adder(String, String, char, int) adder}等方法。<br/>
	 * 例：integerMultiplication("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被乘数
	 * @param operand2 二进制补码表示的乘数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为length+1的字符串表示的相乘结果，其中第1位指示是否溢出（溢出为1，否则为0），后length位是相乘结果
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
	 * 整数的不恢复余数除法，可调用{@link #adder(String, String, char, int) adder}等方法实现。<br/>
	 * 例：integerDivision("0100", "0011", 8)
	 * @param operand1 二进制补码表示的被除数
	 * @param operand2 二进制补码表示的除数
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度，当某个操作数的长度小于length时，需要在高位补符号位
	 * @return 长度为2*length+1的字符串表示的相除结果，其中第1位指示是否溢出（溢出为1，否则为0），其后length位为商，最后length位为余数
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
		//修正商
		String qString = null;
		String rString = null;
		if (operand1.charAt(0) == operand2.charAt(0)) {
			qString = new String(Q).substring(1);
		} else {
			qString = oneAdder(new String(Q).substring(1)).substring(1);
		}
		//修正余数
		if (R.charAt(0) == operand1.charAt(0)) {
			rString = R;
		} else {
			if (operand1.charAt(0) == operand2.charAt(0)) {
				rString = integerAddition(R, normalOperand2, length).substring(1);
			} else {
				rString = integerSubtraction(R, normalOperand2, length).substring(1);
			}
		}
		//确定是否溢出
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
	 * 判断一个数是否全部位为零
	 * @param num 一个二进制串
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
	 * 带符号整数加法，可以调用{@link #adder(String, String, char, int) adder}等方法，
	 * 但不能直接将操作数转换为补码后使用{@link #integerAddition(String, String, int) integerAddition}、
	 * {@link #integerSubtraction(String, String, int) integerSubtraction}来实现。<br/>
	 * 例：signedAddition("1100", "1011", 8)
	 * @param operand1 二进制原码表示的被加数，其中第1位为符号位
	 * @param operand2 二进制原码表示的加数，其中第1位为符号位
	 * @param length 存放操作数的寄存器的长度，为4的倍数。length不小于操作数的长度（不包含符号），当某个操作数的长度小于length时，需要将其长度扩展到length
	 * @return 长度为length+2的字符串表示的计算结果，其中第1位指示是否溢出（溢出为1，否则为0），第2位为符号位，后length位是相加结果
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
			//同号求和
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
		String[] outcome = new String[length / 4]; //分成length/4段来调用4位先行进位加法器
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
	 * 串行进位加法器
	 * 专门为坑爹的浮点数加法而设计
	 * @param operand1 第一个操作数
	 * @param operand2 第二个操作数
	 * @param length 所得结果的要求长度（也是两个操作数的长度）
	 * @return 返回一个长度为1+length的字符串，第一位指示是否溢出，1表示溢出，0表示没有溢出，后length位是加法运算结果
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
	 * 由于考虑不周到，所以必须设计一个有进位的串行进位加法器
	 * 为了坑爹的浮点数减法而设计
	 * @param operand1 第一个操作数字
	 * @param operand2 第二个操作数
	 * @param length 所得结果要求长度
	 * @param cin  最低位进位
	 * @return 返回一个1+length的字符串，第一位指示是否溢出，1表示溢出，0表示没有溢出，后length位是运算结果
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
	 * 串行进位减法器
	 * 专门为坑爹的浮点数减法而设计
	 * 这个破浮点数减法怎么那么麻烦。晕！
	 * @param operand1
	 * @param operand2
	 * @return
	 */
	private String serialSubtraction(String operand1, String operand2) {
		return serialAdderAmend(operand1, negation(operand2), operand1.length(), '1');
	}
	
	/**
	 * 用于计算字符串的进位
	 * @param original 要被进位的操作数
	 * @return 返回字符串长度为original进位后的产物
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
	 * 舍入到偶数
	 * @param original 要被舍入的操作数
	 * @return 返回值和carryBit结构相同
	 */
	private String roundToEven(String original) {
		if (original.endsWith("0")) {
			return original;
		} else {
			return carryBit(original);
		}
	}
	
	/**
	 * 浮点数加法，可调用{@link #signedAddition(String, String, int) signedAddition}等方法实现。<br/>
	 * 加减共同体，嗯。
	 * 如果两个数符号相同就做加法，否则做减法。
	 * 例：floatAddition("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 二进制表示的被加数
	 * @param operand2 二进制表示的加数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相加结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
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
		//计算阶码
		int delta = Integer.valueOf(integerTrueValue(exponent1)) - Integer.valueOf(integerTrueValue(exponent2));
		if (delta > 0) {
			finalExp = exponent1;
		} else {
			finalExp = exponent2;
		}
		if (!exponent1.equals(signExtened("0", gLength)) && !exponent2.equals(signExtened("0", gLength))) { //如果都是规格化数字
			if (sign1 == sign2) {  //如果是两个同号的数字做加法
				finalSign = sign1;
				String extendEnd1 = "1" + end1 + signExtened("0", gLength);
				String extendEnd2 = "1" + end2 + signExtened("0", gLength);
				if (delta >= 0) {
					//第一个数字的阶比较大 只需要处理这种情况就OK
					if (delta >= 1 + sLength + gLength) { //两者阶码差距过大
						return "0" + operand1;
					} else { //如果差距不是特别大，进行运算
						//获取移位以后的第二个操作数字的尾数部分
						if (delta > 0) {
							extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
						} //如果等于0不需要进行移位，直接往下进行操作就可以了
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
								if (tempResult.charAt(0) == '0') { //舍入后不需要进位
									finalEnd = tempResult.substring(2);
								} else { //舍入后变为10.xx，需要检查阶码是否上溢
									if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //阶码上溢
										return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
									} else { //阶码没有上溢出
										finalExp = oneAdder(finalExp).substring(1);
										finalEnd = tempResult.substring(1, 1 + sLength);
									}
								}
							}
						} else { //10.xxx
							if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //阶码上溢
								return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
							} else { //阶码没有上溢出
								finalExp = oneAdder(finalExp).substring(1);
								//进行舍入
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
					//否则就改变参数调用顺序 不需要更改，加法可以交换
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			} else { //如果是两个异号的数字那就做减法
				//finalSign默认是sign1的符号
				String extendEnd1 = "1" + end1 + signExtened("0", gLength);
				String extendEnd2 = "1" + end2 + signExtened("0", gLength);
				if (delta >= 0) {
					if (delta > 0) { //用于把阶对齐
						extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
					}
					String tempResult = serialSubtraction(extendEnd1, extendEnd2);
					if (tempResult.charAt(0) == '1') {
						//够减，后面的就是正规结果
						tempResult = tempResult.substring(1); //去掉标志位
						finalSign = sign1;
						//判断是否为0，如果是全0，那就置为机器0
						if (isZero(tempResult)) {
							return "0" + finalSign + signExtened("0", eLength + sLength);
						} else { //如果不为0，那就进行对阶啥的，还要考虑对阶是否到0，麻烦，不想写
							int count = 0;
							for (int i = 0; i < tempResult.length(); i++) {
								if (tempResult.charAt(0) == '1') {
									break;
								}
								tempResult = tempResult.substring(1) + "0";
								count ++;
							}
							//count是指指数域需要左规的位数
							if (Integer.valueOf(integerTrueValue(finalExp)) > count) {
								//左规以后仍然是规格化数字
								finalExp = integerSubtraction(finalExp, integerRepresentation(String.valueOf(count), eLength), eLength).substring(1);
								//先补个0，求出尾数，需要进行舍入判断，这时候tempResult的长度是2+eLenght+gLength
								tempResult = "0" + tempResult;
								if (tempResult.charAt(2 + sLength) == '0') {
									finalEnd = tempResult.substring(2, 2 + sLength);
								} else {
									for (int i = 3 + sLength; i < tempResult.length(); i++) {
										if (tempResult.charAt(i) == '1') {
											tempResult = carryBit(tempResult.substring(0, 2 + sLength)); 
											//加入变成了 10.xx
											if (tempResult.charAt(0) == '1') {
												finalExp = oneAdder(finalExp).substring(1);
												finalEnd = tempResult.substring(1, 1 + sLength);
											} else { //还是01.xx
												finalEnd = tempResult.substring(2, 2 + sLength);
											}
											break;
										}
										if (i == tempResult.length() - 1) {
											tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
											if (tempResult.charAt(0) == '1') {
												finalExp = oneAdder(finalExp).substring(1);
												finalEnd = tempResult.substring(1, 1 + sLength);
											} else { //还是01.xx
												finalEnd = tempResult.substring(2, 2 + sLength);
											}
										}
									}
								}
								return "0" + finalSign + finalExp + finalEnd;
							} else {
								//左规以后不是规格化数字
								int originalExp = Integer.valueOf(integerTrueValue(finalExp));
								int sub = count - originalExp;
								finalExp = signExtened("0", eLength);
								//求出非规格化尾数部分，这时候tempResult是指以1开头的一个数字
								finalEnd = signExtened("0", sub) + tempResult.substring(0, sLength - sub);
								return "0" + finalSign + finalExp + finalEnd;
							}
						}
					} else {
						//不是很够，相当于第二个数的绝对值大于第一个，所以转换参数顺序
						return floatAddition(operand2, operand1, eLength, sLength, gLength);
					}
				} else {
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			}	
		} else {
			if (!exponent1.equals(signExtened("0", gLength)) && exponent2.equals(signExtened("0", gLength))) { //第二个数字为非规格化数字
				if (sign1 == sign2) {  //如果是两个同号的数字做加法
					//delta是第一个数字的阶码减去第二个数字
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
							if (tempResult.charAt(0) == '0') { //舍入后不需要进位
								finalEnd = tempResult.substring(2);
							} else { //舍入后变为10.xx，需要检查阶码是否上溢
								if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //阶码上溢
									return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
								} else { //阶码没有上溢出
									finalExp = oneAdder(finalExp).substring(1);
									finalEnd = tempResult.substring(1, 1 + sLength);
								}
							}
						}
					} else { //10.xxx
						if (finalExp.equals(signExtened("1", eLength - 1) + "0")) { //阶码上溢
							return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
						} else { //阶码没有上溢出
							finalExp = oneAdder(finalExp).substring(1);
							//进行舍入
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
				} else { //如果是两个异号的数字做减法，第一个是规格化数字，第二个是非规格化数字
					String extendEnd1 = "1" + end1 + signExtened("0", gLength);
					String extendEnd2 = "0" + end2 + signExtened("0", gLength);
					if (delta > sLength + gLength) {
						return "0" + operand1;
					}
					extendEnd2 = signExtened("0", delta) + extendEnd2.substring(0, 1 + sLength + gLength - delta);
					String tempResult = serialSubtraction(extendEnd1, extendEnd2);
					//够减，后面的就是正规结果
					tempResult = tempResult.substring(1); //去掉标志位
					finalSign = sign1;
					//判断是否为0，如果是全0，那就置为机器0
					if (isZero(tempResult)) {
						return "0" + finalSign + signExtened("0", eLength + sLength);
					} else { //如果不为0，那就进行对阶啥的，还要考虑对阶是否到0，麻烦，不想写
						int count = 0;
						for (int i = 0; i < tempResult.length(); i++) {
							if (tempResult.charAt(0) == '1') {
								break;
							}
							tempResult = tempResult.substring(1) + "0";
							count ++;
						}
						//count是指指数域需要左规的位数
						if (Integer.valueOf(integerTrueValue(finalExp)) > count) {
							//左规以后仍然是规格化数字
							finalExp = integerSubtraction(finalExp, integerRepresentation(String.valueOf(count), eLength), eLength).substring(1);
							//先补个0，求出尾数，需要进行舍入判断，这时候tempResult的长度是2+eLenght+gLength
							tempResult = "0" + tempResult;
							if (tempResult.charAt(2 + sLength) == '0') {
								finalEnd = tempResult.substring(2, 2 + sLength);
							} else {
								for (int i = 3 + sLength; i < tempResult.length(); i++) {
									if (tempResult.charAt(i) == '1') {
										tempResult = carryBit(tempResult.substring(0, 2 + sLength)); 
										//加入变成了 10.xx
										if (tempResult.charAt(0) == '1') {
											finalExp = oneAdder(finalExp).substring(1);
											finalEnd = tempResult.substring(1, 1 + sLength);
										} else { //还是01.xx
											finalEnd = tempResult.substring(2, 2 + sLength);
										}
										break;
									}
									if (i == tempResult.length() - 1) {
										tempResult = roundToEven(tempResult.substring(0, 2 + sLength));
										if (tempResult.charAt(0) == '1') {
											finalExp = oneAdder(finalExp).substring(1);
											finalEnd = tempResult.substring(1, 1 + sLength);
										} else { //还是01.xx
											finalEnd = tempResult.substring(2, 2 + sLength);
										}
									}
								}
							}
							return "0" + finalSign + finalExp + finalEnd;
						} else {
							//左规以后不是规格化数字
							int originalExp = Integer.valueOf(integerTrueValue(finalExp));
							int sub = count - originalExp;
							finalExp = signExtened("0", eLength);
							//求出非规格化尾数部分，这时候tempResult是指以1开头的一个数字
							finalEnd = signExtened("0", sub) + tempResult.substring(0, sLength - sub);
							return "0" + finalSign + finalExp + finalEnd;
						}
					}
				}
			} else if (exponent1.equals(signExtened("0", gLength)) && !exponent2.equals(signExtened("0", gLength))) { //第一个为非规格化数字
				if (sign1 == sign2) {  //如果是两个同号的数字做加法，其实只需要交换两个参数的位置就可以了
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				} else { //如果是两个异号的数字做减法
					return floatAddition(operand2, operand1, eLength, sLength, gLength);
				}
			} else { //都是非规格化数字
				if (sign1 == sign2) {  //如果是两个同号的数字做加法
					String tempResult = serialAdder(end1, end2, sLength);
					if (tempResult.charAt(0) == '1') { //加法溢出，也就是可以摆脱了非规格化的状态
						finalExp = signExtened("0", eLength - 1) + "1"; 
					}
					finalEnd = tempResult.substring(1);
					return "0" + finalSign + finalExp + finalEnd;
				} else { //如果是两个异号的数字做减法
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
	 * 浮点数减法，可调用{@link #floatAddition(String, String, int, int, int) floatAddition}方法实现。<br/>
	 * 例：floatSubtraction("00111111010100000", "00111111001000000", 8, 8, 8)
	 * @param operand1 二进制表示的被减数
	 * @param operand2 二进制表示的减数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @param gLength 保护位的长度
	 * @return 长度为2+eLength+sLength的字符串表示的相减结果，其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatSubtraction (String operand1, String operand2, int eLength, int sLength, int gLength) {
		return floatAddition(operand1, ((operand2.charAt(0) == '1') ? '0' : '1') + operand2.substring(1), eLength, sLength, gLength);
	}
	
	/**
	 * 专门为麻烦的浮点数乘法而设计
	 * (浮点计算麻烦+烦人)
	 * @param operand1 第一个操作数 0.xx
	 * @param operand2 第二个操作数 0.xx
	 * @param length 长度 小数点后的长度
	 * @return 返回0.xx，xx的长度为2n位
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
	 * 浮点数乘法，可调用{@link #integerMultiplication(String, String, int) integerMultiplication}等方法实现。<br/>
	 * 例：floatMultiplication("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被乘数
	 * @param operand2 二进制表示的乘数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
	 */
	public String floatMultiplication (String operand1, String operand2, int eLength, int sLength) {
		//判断是否为0
		if (isZero(operand1.substring(1)) || isZero(operand2.substring(2))) {
			return signExtened("0", 2 + eLength + sLength);
		}
		//处理符号问题
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		char finalChar;
		if (sign1 == sign2) {
			finalChar = '0';
		} else {
			finalChar = '1';
		}
		//指数域的必要的准备工作
		String exponent1 = operand1.substring(1, 1 + eLength);
		String exponent2 = operand2.substring(1, 1 + eLength);
		String finalExponent = null;
		int exp1 = Integer.valueOf(integerTrueValue(exponent1)) - (1 << (eLength - 1)) + 1;
		int exp2 = Integer.valueOf(integerTrueValue(exponent2)) - (1 << (eLength - 1)) + 1;
		int finalEXP = exp1 + exp2;
		int maxExp = (1 << eLength) - 1;
		int formMinExp =  - maxExp + 1;
		int informMinExp = formMinExp - sLength;
		//尾数
		String end1 = operand1.substring(1 + eLength);
		String end2 = operand2.substring(1 + eLength);
		String finalEnd = null;
		if (exp1 >= formMinExp && exp2 >= formMinExp) { //两个规格化数字
			String extendEnd1 = "01" + end1; //化成0.1xx * 0.1xx
			exp1++;
			String extendEnd2 = "01" + end2;
			exp2++;
			String tempResult = Multiplication(extendEnd1, extendEnd2, extendEnd1.length() - 1); //0.xx * 0.xx = 0.xxxx
			int count = 1;
			for (int i = 1; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //把所有的0都割掉
					break;
				}
				count++;
			}
			tempResult = "0" + tempResult;
			//补充成2sLength+2，进行舍入
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
				//进行完了进位，判断是否变成10.xx
				if (tempResult.charAt(0) == '1') {
					count --;
					finalEnd = tempResult.substring(1, 1 + sLength);
				} else {
					finalEnd = tempResult.substring(2 , 2 + sLength);
				}
			}
			finalEXP = exp1 + exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalChar + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp){
				return "0" + finalChar + signExtened("0", sLength + eLength); //向下溢出
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) {
				//非规格化数字
				finalExponent = signExtened("0", eLength);
				int delta = formMinExp - finalEXP;
				finalEnd = "1" + finalEnd;
				for (int i = 0; i < delta - 1; i++) {
					finalEnd = "0" + finalEnd;
				}
				finalEnd = finalEnd.substring(0, sLength);
				return "0" + finalChar + finalExponent + finalEnd; 
			} else {
				//规格化数字
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalChar + finalExponent + finalEnd;
			}
		} else if (exp1 >= formMinExp && !(exp2 >= formMinExp)) {
			//第二个数字为非规格化数字
			String extendEnd1 = "01" + end1; //化成0.1xx * 0.0xx，相当于分别右移了一位
			exp1++;
			String extendEnd2 = "00" + end2;
			exp2++;
			String tempResult = Multiplication(extendEnd1, extendEnd2, extendEnd1.length() - 1); //0.xx * 0.xx = 0.xxxx
			int count = 1;
			for (int i = 1; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //把所有的0都割掉
					break;
				}
				count++;
			}
			tempResult = "0" + tempResult;
			//补充成2sLength+2，进行舍入
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
				//进行完了进位，判断是否变成10.xx
				if (tempResult.charAt(0) == '1') {
					count --;
					finalEnd = tempResult.substring(1, 1 + sLength);
				} else {
					finalEnd = tempResult.substring(2 , 2 + sLength);
				}
			}
			finalEXP = exp1 + exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalChar + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp){
				return "0" + finalChar + signExtened("0", sLength + eLength); //向下溢出
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) {
				//非规格化数字
				finalExponent = signExtened("0", eLength);
				int delta = formMinExp - finalEXP;
				finalEnd = "1" + finalEnd;
				for (int i = 0; i < delta - 1; i++) {
					finalEnd = "0" + finalEnd;
				}
				finalEnd = finalEnd.substring(0, sLength);
				return "0" + finalChar + finalExponent + finalEnd; 
			} else {
				//规格化数字
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalChar + finalExponent + finalEnd;
			}
		} else if (exp2 >= formMinExp && !(exp1 >= formMinExp)) {
			//第一个数字为非规格化数字，转换调用顺序进行求解
			return floatMultiplication(operand2, operand1, eLength, sLength);
		} else { //假如说2个都是非规格化数字
			//可以劝退了，不用乘了，直接返回0就可以了
			return "0" + finalChar + signExtened("0", sLength + eLength);
		}
	}
	
	/**
	 * 为了浮点数乘除法而设计
	 * 把某个数字转化成浮点数指数移码
	 * @param exponent 指数值
	 * @param length 浮点数指数域长度
	 * @return
	 */
	private String toExponent(int exponent, int length) {
		exponent = exponent + (1 << (length - 1)) - 1;
		return integerRepresentation(String.valueOf(exponent), length + 1).substring(1);
	}
	
	/**
	 * 专门为浮点数除法设计
	 * 浮点数除法也太坑了吧
	 * @param operand1 第一个操作数 0.1xx
	 * @param operand2 第二个操作数 0.1xx
	 * @param length 长度
	 * @return 结果, 长度也为length
	 */
	private String division(String operand1, String operand2, int length) {
		String rAndQ = operand1 + signExtened("0", length);
		String neOperand2 = oneAdder(negation(operand2)).substring(1);
		String temp = null;
		temp = rAndQ.substring(0, length);
		String r1 = serialAdder(temp, neOperand2, length).substring(1);
		if (r1.charAt(0) == '0') { //不等于0恢复余数，相当于啥都没做，等于0说明结果是1.xx
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
	 * 浮点数除法，可调用{@link #integerDivision(String, String, int) integerDivision}等方法实现。<br/>
	 * 例：floatDivision("00111110111000000", "00111111000000000", 8, 8)
	 * @param operand1 二进制表示的被除数
	 * @param operand2 二进制表示的除数
	 * @param eLength 指数的长度，取值大于等于 4
	 * @param sLength 尾数的长度，取值大于等于 4
	 * @return 长度为2+eLength+sLength的字符串表示的相乘结果,其中第1位指示是否指数上溢（溢出为1，否则为0），其余位从左到右依次为符号、指数（移码表示）、尾数（首位隐藏）。舍入策略为向0舍入
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
		 * 鉴于浮点数除法的工作量巨大，我觉得不采用任何有技巧的舍入方式，打算直接截断。
		 * 不考虑任何精度！
		 */
		char sign1 = operand1.charAt(0);
		char sign2 = operand2.charAt(0);
		char finalSign = '1';
		if (sign1 == sign2) {
			finalSign = '0';
		}
		//指数域的必要的准备工作
		String exponent1 = operand1.substring(1, 1 + eLength);
		String exponent2 = operand2.substring(1, 1 + eLength);
		String finalExponent = null;
		int exp1 = Integer.valueOf(integerTrueValue(exponent1)) - (1 << (eLength - 1)) + 1;
		int exp2 = Integer.valueOf(integerTrueValue(exponent2)) - (1 << (eLength - 1)) + 1;
		int finalEXP = exp1 + exp2;
		int maxExp = (1 << eLength) - 1;
		int formMinExp =  - maxExp + 1;
		int informMinExp = formMinExp - sLength;
		//尾数
		String end1 = operand1.substring(1 + eLength);
		String end2 = operand2.substring(1 + eLength);
		String finalEnd = null;
		//考虑下除0或者0除以其他的情况
		if (operand1.substring(1).equals(signExtened("0", sLength + eLength))) {
			return "0" + finalSign + signExtened("0", sLength + eLength);
		} else if (operand2.substring(1).equals(signExtened("0", sLength + eLength))) {
			return "0" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
		}
		if (exp1 >= formMinExp && exp2 >= formMinExp) { //两个都是规格化数字
			String extendEnd1 = "01" + end1; //0.1x
			exp1++;
			String extendEnd2 = "01" + end2; //0.1x
			exp2++;
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //一个2sLength+1位的商0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //去除掉前面所有的0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //太小了
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //非规格化数字
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//规格化数字
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else if (exp1 >= formMinExp && !(exp2 >= formMinExp)) { //第二个是非规格化数字
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
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //一个2sLength+1位的商0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //去除掉前面所有的0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //太小了
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //非规格化数字
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//规格化数字
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else if (!(exp2 >= formMinExp) && exp1 >= formMinExp) { //第一个是非规格化数字
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
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //一个2sLength+1位的商0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //去除掉前面所有的0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //太小了
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //非规格化数字
 				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//规格化数字
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		} else { //两个都是非规格化数字
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
			String tempResult = division(extendEnd1 + signExtened("0", extendEnd1.length()), extendEnd2 + signExtened("0", extendEnd1.length()), 2 * extendEnd1.length()); //一个2sLength+1位的商0.xx
			int count = 0;
			for (int i = 0; i < tempResult.length(); i++) {
				if (tempResult.charAt(i) == '1') {
					tempResult = tempResult.substring(i); //去除掉前面所有的0
					break;
				}
				count++;
			}
			for (int i = tempResult.length(); i < 2 * sLength; i++) {
				tempResult = tempResult + "0";
			}
			finalEXP = exp1 - exp2 - count;
			if (finalEXP > maxExp) { //溢出
				return "1" + finalSign + signExtened("1", eLength) + signExtened("0", sLength);
			} else if (finalEXP < informMinExp) { //太小了
				return "0" + "0" + signExtened("0", sLength + eLength);
			} else if (finalEXP >= informMinExp && finalEXP < formMinExp) { //非规格化数字
				finalExponent = signExtened("0", eLength);
 				for (int i = finalEXP; i < formMinExp; i++) {
 					tempResult = "0" + tempResult;
 				}
 				finalEnd = tempResult.substring(1, 1 + sLength);
 				return "0" + finalSign + finalExponent + finalEnd;
			} else {
				//规格化数字
				finalEnd = tempResult.substring(1, 1 + sLength);
				finalExponent = toExponent(finalEXP, eLength);
				return "0" + finalSign + finalExponent + finalEnd;
			}
		}
	}
}
