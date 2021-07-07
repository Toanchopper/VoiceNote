package com.example.voicenote;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class ShannoFano {

    public ShannoFano()
    {

    }
    //sắp xếp tăng dần sử dụng selection sort để chuẩn bị mã hóa
    public static Object[] doSelectionSort(char[] alphabet, double[] alphabetProbability, int[] counters){

        for (int i = 0; i < alphabetProbability.length - 1; i++)
        {
            /* sort 'alphabetProbability' array then use same indexes to other arrays
             *  because element of a probability array corresponds to the element of alphabet array
             *  the same with counters array
             */
            int index = i;
            for (int j = i + 1; j < alphabetProbability.length; j++)
                if (alphabetProbability[j] > alphabetProbability[index])
                    index = j;

            double smallerNumber = alphabetProbability[index];
            alphabetProbability[index] = alphabetProbability[i];
            alphabetProbability[i] = smallerNumber;

            char change = alphabet[index];
            alphabet[index] = alphabet[i];
            alphabet[i] = change;

            int temp = counters[index];
            counters[index] = counters[i];
            counters[i] = temp;

        }
        //return Object containing three updated arrays
        return new Object[]{alphabet, alphabetProbability, counters};
    }

    //If there is no letters in the text we don't need to encode them, so delete all elements with zero probability
    //xóa tất cả phần tử có xác suất là 0
    public static Object[] deleteExtra( char[] sortedAlphabet, double[] sortedProbability, int[] sortedCounters){
        int j = 0,
                count = 0;
        //create copies of arrays with new sizes, because when unnecessary elements deleted arrays shrink in size
        for( int i = 0;  i < sortedProbability.length;  i++ )
        {
            if (sortedProbability[i] != 0){
                count++;
            }
        }

        char[] newAlphabet = new char[count];
        double[] newProbability = new double[count];
        int[] newCounters = new int[count];
        //if an element has non-zero probability add it to new array
        //nếu 1 phần tử có xs khác 0 thì add nó vào mảng r sx
        //same as sorting, all array will have same indexes changed
        for( int i = 0;  i < sortedProbability.length;  i++ )
        {
            if (sortedProbability[i] != 0){
                newProbability[j] = sortedProbability[i];
                newAlphabet[j] = sortedAlphabet[i];
                newCounters[j++] = sortedCounters[i];
            }
        }
        //return object of new arrays with non-zero probabilities
        return new Object[]{newAlphabet, newProbability, newCounters};
    }

    //chechArray is used to print current states of arrays, mostly used after updating arrays
    public static void checkArray(char[] alphabet, double[] prob, String[] code){
        System.out.println("\nCHECK sorted Array: ");
        for(int i = 0; i < alphabet.length; i++){
            System.out.printf("\n%s - %.4f - %s", alphabet[i], prob[i], code[i]);
        }
    }

    public static String[] Encode(int midpoint, int[] sortedCounters, String[] code, int top, int bot){
        int upcounter = 0;
        int downcounter = 0;
        for (int i = top; i <= midpoint; i++){
            code[i] += "1";
            upcounter++;
        }

        for (int i = midpoint + 1; i < bot; i++){
            code[i] += "0";
            downcounter++;
        }
        System.out.printf("\nupcounter:%d, downcounter:%d, mid:%d, top:%d, bot:%d", upcounter, downcounter, midpoint, top, bot);
        if (upcounter>1){
            int sum = 0;
            for(int i = top; i <= midpoint; i++){
                sum=sum+sortedCounters[i];
            }
            double	tempX = 0,
                    tempY = 738;
            int sum1 = 0,
                    mid = 0;

            for (int i = top; i <= midpoint; i++){
                sum1 = sum1 + sortedCounters[i];
                tempX = Math.abs( sum/2 - sum1 );
                if (tempX < tempY){
                    tempY = tempX;
                    mid = i;
                }
            }
            Encode(mid, sortedCounters, code, top, midpoint+1);
        }
        if (downcounter > 1){
            int sum = 0;
            for (int i = midpoint+1; i < bot; i++){
                sum = sum + sortedCounters[i];
            }
            double	tempX = 0,
                    tempY = 738;

            int sum1 = 0,
                    mid = 0;

            for (int i = midpoint+1; i < bot; i++){
                sum1 = sum1 + sortedCounters[i];
                tempX = Math.abs( sum/2 - sum1 );
                if (tempX < tempY){
                    tempY = tempX;
                    mid = i;
                }
            }
            Encode(mid, sortedCounters, code, midpoint+1, bot);
        }
        return code;
    }

    public static String Decode(String encodedText, String[] code, char[] alphabet){
        String decodedText = "";
        Scanner fileReader = new Scanner(encodedText).useLocale(Locale.US);
        String text = "";
        while (fileReader.hasNextLine()){
            text = fileReader.nextLine();
            String codeCheck = "";
            for (int k = 0; k < text.length(); k++){
                codeCheck += text.charAt(k);
                for (int i = 0; i < code.length; i++){
                    if(codeCheck.equals(code[i])){
                        decodedText += alphabet[i];
                        codeCheck = "";
                    }
                }
            }
        }
        return decodedText;
    }

    public static boolean binaryToBool(char bit){
        if(bit == '1')
            return true;
        else
            return false;
    }

    public static String boolToBinary(boolean bit){
        if(bit)
            return "1";
        else
            return "0";
    }

    public static int genRandom(){
        Random r = new Random();
        int Low = 0;
        int High = 4;
        return r.nextInt(High-Low) + Low;
    }

    public static char changeBit(char bit){
        if(bit == '1')
            return '0';
        else
            return '1';
    }

    public static String[] addErrors(String[] encodedText){

        for(int i = 0; i < encodedText.length; i++){
            String codeBlock = encodedText[i];
            //change bit at a random place
            int r = genRandom();
            StringBuilder codeBlocke = new StringBuilder(codeBlock);
            codeBlocke.setCharAt(r, changeBit(codeBlock.charAt(r)));

            encodedText[i] = codeBlocke.toString();
        }
        return encodedText;
    }

    //check error syndrome, at any case return index of a bit which has error
    public static int checkErrorSyndrome(String errorSyndrome){
        //if errorBit is 9, then there is no error
        int errorBit = 9;
        switch(errorSyndrome){
            case "000":	errorBit = 9;
                break;
            case "001":	errorBit = 6;
                break;
            case "010":	errorBit = 5;
                break;
            case "011":	errorBit = 3;
                break;
            case "100":	errorBit = 4;
                break;
            case "101":	errorBit = 0;
                break;
            case "110":	errorBit = 2;
                break;
            case "111": 	errorBit = 1;
                break;
        }
        return errorBit;
    }

    public static String[] decodeHamming(String[] codeWithErrors){

        for(int i = 0; i < codeWithErrors.length; i++){
            String codeBlock = codeWithErrors[i];
            boolean i1 = binaryToBool(codeBlock.charAt(0)),
                    i2 = binaryToBool(codeBlock.charAt(1)),
                    i3 = binaryToBool(codeBlock.charAt(2)),
                    i4 = binaryToBool(codeBlock.charAt(3)),
                    r1 = binaryToBool(codeBlock.charAt(4)),
                    r2 = binaryToBool(codeBlock.charAt(5)),
                    r3 = binaryToBool(codeBlock.charAt(6));
            //calculate error syndrome using formulas for s1,s2,s3. ^ - XOR
            String  s1 = boolToBinary(r1 ^ i1 ^ i2 ^ i3),
                    s2 = boolToBinary(r2 ^ i2 ^ i3 ^ i4),
                    s3 = boolToBinary(r3 ^ i1 ^ i2 ^ i4);

            String errorSyndrome = s1 + s2 + s3;
            //use function checkErrorSyndrome to..well, check error syndrome
            int index = checkErrorSyndrome(errorSyndrome);
            //use changeBit to change bit at given index in codeBlock
            StringBuilder codeBlocke = new StringBuilder(codeBlock);
            codeBlocke.setCharAt(index, changeBit(codeBlock.charAt(index)));
            //System.out.print("\n" + codeBlocke.toString());
            codeWithErrors[i] = codeBlocke.toString();

        }
        //return new String array of codes with errors
        return codeWithErrors;
    }

    //we need parity bits when working with errors, we don't need them during calculations
    public static String[] deleteParity(String[] code){
        String[] newCode = new String[code.length];

        for(int i = 0; i < code.length; i++){
            String codeBlock = code[i];
            char i1 = codeBlock.charAt(0),
                    i2 = codeBlock.charAt(1),
                    i3 = codeBlock.charAt(2),
                    i4 = codeBlock.charAt(3),
                    r1 = codeBlock.charAt(4),
                    r2 = codeBlock.charAt(5),
                    r3 = codeBlock.charAt(6);
            String codeBlocke = "";
            codeBlocke += i1;
            codeBlocke += i2;
            codeBlocke += i3;
            codeBlocke += i4;
            newCode[i] = codeBlocke;
        }

        return newCode;
    }


    public List<String[]> kqmh(String content)
    {
        List<String[]> list= new ArrayList<String[]>();
        int[] alphabetCount = new int[67];
        char[] alphabet = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ,!.?0123456789 ".toCharArray();
        double[] alphabetProbability = new double[67];
            //đếm số lần xuất hiện các ký tự
            char[] tempText = content.toCharArray();
            for (int i = 0; i < content.length(); i++){
                for (int j = 0; j < alphabet.length; j++){
                    if (tempText[i] == alphabet[j]){
                        alphabetCount[j]++;
                    }
                }
            }
            //tính tổng ký tự
            double sum = 0;
            for (int l = 0; l < alphabetCount.length; l++){
                sum += alphabetCount[l];
            }
            //tần suất xhien
            for (int p = 0; p < alphabetCount.length; p++){
                alphabetProbability[p] = (alphabetCount[p]/sum);
            }

        //sắp xếp dãy theo tần xuất xhien
        Object[] sortedArrays = doSelectionSort(alphabet, alphabetProbability, alphabetCount);

        char[] sortedAlphabet = (char[]) sortedArrays[0];
        double[] sortedProbability = (double[]) sortedArrays[1];
        int[] sortedCounters = (int[]) sortedArrays[2];

        //print sorted array
        //System.out.println("\nNew sorted Array: ");
        // xuất mảng sau khi xóa các ký tự thừa
        Object[] noExtraArrays = deleteExtra(sortedAlphabet, sortedProbability, sortedCounters);
        char[] newAlphabet = (char[]) noExtraArrays[0];
        double[] newProbability = (double[]) noExtraArrays[1];
        int[] newCounters = (int[]) noExtraArrays[2];

        //part 2
        double probsum = 0;
        // số lượng các xác suất
        int sum2 = 0,
                numberOfElements = newProbability.length;
        for (int i = 0; i < numberOfElements; i++){
            //tổng sx
            probsum += newProbability[i];
            //tổng ký tự
            sum2 += newCounters[i];
        }

        String[] code = new String[numberOfElements];
        for (int i = 0; i < code.length; i++){
            code[i] = "";
        }
        double	tempX = 0,
                tempY = 738;
        int sum1 = 0,
                mid = 0;


        for (int i = 0; i < numberOfElements; ++i){
            sum1 = sum1 + newCounters[i];
            tempX = Math.abs(sum/2 - sum1);
            if (tempX < tempY){
                tempY = tempX;
                mid = i;
            }
        }
        //System.out.printf("\nsum:%d, nOE:%d, mid:%d", sum, numberOfElements, mid);
        // xuất dãy nhị phân dựa trên cây nhị phân theo thứ tự
        code = Encode(mid, newCounters, code, 0, numberOfElements);

        /////tính R trung bình
        DecimalFormat df=new DecimalFormat("0.00");
        long n=content.length();
        int usage=0;
        for(int i=0;i<code.length;i++)
        {
            usage+=code[i].length()*newCounters[i];
        }
        float average=(float) usage/n;
        ////tính Entropy
        float entropy=0;
        for(int i=0;i<newProbability.length;i++)
        {
            double rs=Math.log(1/newProbability[i])/Math.log(2);
            entropy+=newProbability[i]*rs;
        }
        ////số bit mã hóa thông thường
        double normalbit=Math.log((double) n)/Math.log(2);

        //part2 encode text
        // chuỗi nhị phân mới dựa vào bản rõ
        String encodedText = "";
        char[] tempText2 = content.toCharArray();
        for (int i = 0; i < content.length(); i++){
            for (int j = 0; j < newAlphabet.length; j++){
                if (tempText[i] == newAlphabet[j]){
                    encodedText += code[j];
                    //encodedText += " ";
                }
            }
        }
        //calculate exact number of blocks of 4 bits
        //tổng ký tự chuỗi np của bản rõ
        int length = encodedText.length();
        //chia các khối mỗi khối 4 bits
        int lengthBy4 = length/4;
        //if there are extra bits add 1 to total length
        if(length%4 != 0)
            lengthBy4 += 1;
        String dataBits = "";
        //mảng bit mới có số phần tử là số lượng block 4 bits
        String[] newEncodedText = new String[lengthBy4];
        //chuyển các khối block 4 bit vào làm phần tử của mảng mới
        int c = 0, l = 0;
        for (int k = 0; k < length; k++){
            dataBits += encodedText.charAt(k);
            c++;
            if(c == 4){
                newEncodedText[l] = dataBits;
                l++;
                c = 0;
                dataBits = "";
            }
            else if(k == (length - 1) && c != 4){
                if(c == 1){
                    newEncodedText[l] = dataBits + "000";
                }else if(c == 2){
                    newEncodedText[l] = dataBits + "00";
                }else if(c == 3){
                    newEncodedText[l] = dataBits + "0";
                }
            }


        }
        //check
         // them các bit phụ vào khối
        //--Adding parity bit to blocks
        for(int i = 0; i < newEncodedText.length; i++){
            String codeBlock = newEncodedText[i];
            boolean i1 = binaryToBool(codeBlock.charAt(0)),
                    i2 = binaryToBool(codeBlock.charAt(1)),
                    i3 = binaryToBool(codeBlock.charAt(2)),
                    i4 = binaryToBool(codeBlock.charAt(3));
            //-Calculating parity bits

            String	r1 = boolToBinary(i1^i2^i3),
                    r2 = boolToBinary(i2^i3^i4),
                    r3 = boolToBinary(i1^i2^i4);

            newEncodedText[i] = codeBlock + r1 + r2 + r3;
        }


        //PART5

        //Adding errors
        String[] codeWithErrors = addErrors(newEncodedText);
        String [] codeWEP = deleteParity(codeWithErrors);

        String encodedText3 = "";
        for(int i = 0; i < codeWEP.length; i++){
            encodedText3 += codeWEP[i];
        }
        //Decode and print decoded text with errors
        String decodedText3 = Decode(encodedText3, code, newAlphabet);
        String[] kq=new String[]{String.valueOf(df.format(average)),String.valueOf(df.format(entropy)),encodedText3,decodedText3};
        list.add(kq);
        list.add(code);
        String[] temp=new String[newAlphabet.length];
        for(int i=0;i<temp.length;i++)
        {
            temp[i]=String.valueOf(newAlphabet[i]);
        }
        list.add(temp);
        list.add(codeWithErrors);
        return  list;
    }

    public String kqgm(String[] code,char[] newAlphabet,String[] matext)
    {
        String[] codeWithoutErrors = decodeHamming(matext);
        String[] codeWOEP = deleteParity(codeWithoutErrors);
        //print code without errors
        String encodedText2 = "";
        for(int i =0; i < codeWOEP.length; i++){
            encodedText2 += codeWOEP[i];
        }
        //decode and print decoded text
        String decodedText2 = Decode(encodedText2, code, newAlphabet);
        return decodedText2;
    }
}
