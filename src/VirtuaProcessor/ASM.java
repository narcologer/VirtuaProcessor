/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VirtuaProcessor;

/**
 *
 * @author User
 */

public class ASM {
    int cmd;
    int pc;
    int cmem[]=new int[1024];
    int dmem[]=new int[1024];
    int reg[]= new int[8];
    int x[] = new int[7]; //массив-константа
    int literal;
    

   public String[] input(String text) {

       String[] arr=text.split("\n");
       return arr;
   }
   
   public String input_single(String text) {
       String[] arr=text.split("\n");
       return arr[pc];
   }
   
   public void CmemFiller(String text) {
       
       String TotalToCMEM = "";
       String TotalToDMEM = "";
       String[] newarr;
       String[] operands;
           newarr=text.split(" ");
           if (newarr[0].equals("mov")) 
               TotalToCMEM+="1"; // в начале кода операции - ее назначение
           else if (newarr[0].equals("add"))
               TotalToCMEM+="2";
           else if (newarr[0].equals("sub"))
               TotalToCMEM+="3";
           else if (newarr[0].equals("end"))
               TotalToCMEM+="4";
           else if (newarr[0].equals("cmp"))
               TotalToCMEM+="5";
           else if (newarr[0].equals("beq"))
               TotalToCMEM+="6";
           else if (newarr[0].equals("jmp")) // хотел bra назвать
               TotalToCMEM+="7";
           if (!(TotalToCMEM.equals("4"))&&newarr[1].indexOf(",")!=-1)
       {
               //Запятая есть
               operands=newarr[1].split(",");
           {
               if (TotalToCMEM.equals("5")) //работа со сравнением
               { // сравниваем только два регистра, с числом сравнивать можно, но лень
                    TotalToDMEM+=operands[0].charAt(1); //номер 1 регистра
                    TotalToDMEM+=operands[1].charAt(1); //номер 2 регистра
               }
               else
               { // работа с move,add и sub
                if (operands[0].indexOf("d")==0) // если первый операнд - регистр
                {
                    TotalToCMEM+="1"; // Литерал 1 - действие содержимого 1 регистра с содержимым 2 регистра.
                    TotalToDMEM+=operands[0].charAt(1); //номер 1 регистра
                    TotalToDMEM+=operands[1].charAt(1); //номер 2 регистра
                }
                else if (operands[0].indexOf("#")==0) // иначе если первый операнд - число
                {
                    TotalToCMEM+="2"; // Литерал 2 - действие числа с содержимым 2 регистра.
                    TotalToDMEM+=operands[0].substring(1, operands[0].length()); // число
                    TotalToDMEM+=operands[1].charAt(1); //номер 2 регистра
                }
                else if ((operands[0].contains("["))&&(operands[0].contains("]"))) // если в первом операнде содержится ячейка массива
                {
                    TotalToCMEM+="3"; //Литерал 3 - действие члена массива с регистром
                    String reg=operands[0].substring(operands[0].indexOf("[")+1,operands[0].indexOf("]"));
                    TotalToDMEM+=reg.charAt(1); //номер регистра, стоящего между скобками
                    TotalToDMEM+=operands[1].charAt(1); //номер 2 регистра
                }
               }
           }
       }
           else
               //Запятой нет
           {
               if (TotalToCMEM.equals("6")) //работа с бранчем beq
               {
                   TotalToDMEM=newarr[1]; // Отправляем в DMEM номер строки, куда посылает бранч
                   if ((cmem[pc-1]%10)==1)
                       TotalToCMEM+="1"; // Литерал 1 - переходить можно
                   else
                       TotalToCMEM+="2"; // Литерал 2 - переходить нельзя
               }
               else if (TotalToCMEM.equals("7")) //работа с бранчем jmp
               {
                   TotalToCMEM+="0";
                   TotalToDMEM=newarr[1]; // Отправляем в DMEM номер строки, куда посылает бранч
                   // это бранч безусловного перехода, так что больше мы тут ничего не делаем
               }
           }
           // формат кода операции: назначение_литерал_число/регистр_регистр:
           // Так как используется Гарвардская архитектура, то первые 2 числа идут в cmem, а вторые в dmem, как на 2 разные шины
           cmem[pc]=Integer.parseInt(TotalToCMEM);
           if (TotalToCMEM.equals("4"))
               dmem[pc]=0;
           else
               dmem[pc]=Integer.parseInt(TotalToDMEM);
           TotalToCMEM="";
           TotalToDMEM="";
   }
   
   public void cmdexecutioner()
   {
       int literal=0;
       int firstop;
       int secondop;
       int cmdtype = Integer.parseInt(Integer.toString(cmem[pc]).substring(0, 1));
           if (!((cmdtype==5)||(cmdtype==6)||(cmdtype==7))) //т.е запятой нет
                literal = Integer.parseInt(Integer.toString(cmem[pc]).substring(1, 2));
           switch(cmdtype) 
           {
               case 1: // mov
               {
                   if (literal==1)
                   {
                    char c=String.valueOf(dmem[pc]).charAt(1);
                    secondop = Integer.parseInt(String.valueOf(c));
                    c=String.valueOf(dmem[pc]).charAt(0);
                    firstop = Integer.parseInt(String.valueOf(c));
                    reg[secondop]=reg[firstop];
                   }
                   else if (literal==2)
                   {
                    String as = Integer.toString(dmem[pc]);
                    int Number = Integer.parseInt(as.substring(0, as.length()-1));
                    reg[dmem[pc]%10]=Number;
                   }
                   else if (literal==3)
                   {
                    char c=String.valueOf(dmem[pc]).charAt(1);
                    secondop = Integer.parseInt(String.valueOf(c));
                    c=String.valueOf(dmem[pc]).charAt(0);
                    firstop = Integer.parseInt(String.valueOf(c));
                    reg[secondop]=x[reg[firstop]];
                   }
                   pc+=1;
                   break;
                }
               case 2: // add
               {
                   if (literal==1)
                   {
                    char c=String.valueOf(dmem[pc]).charAt(1);
                    secondop = Integer.parseInt(String.valueOf(c));
                    c=String.valueOf(dmem[pc]).charAt(0);
                    firstop = Integer.parseInt(String.valueOf(c));
                    reg[secondop]=reg[secondop]+reg[firstop];
                   }
                   else if (literal==2)
                   {
                    int Number = Integer.parseInt(Integer.toString(dmem[pc]).substring(0, Integer.toString(dmem[pc]).length()-1));
                    reg[dmem[pc]%10]=reg[dmem[pc]%10]+Number;
                   }
                   else if (literal==3)
                   {
                      int regNumber = Integer.parseInt(Integer.toString(dmem[pc]).substring(0, Integer.toString(dmem[pc]).length()-1));
                      reg[dmem[pc]%10]=reg[dmem[pc]%10]+x[reg[regNumber]];
                   }
                   pc+=1;
                   break;
                }
               case 3: // sub
               {
                   if (literal==1)
                   {
                    char c=String.valueOf(dmem[pc]).charAt(1);
                    secondop = Integer.parseInt(String.valueOf(c));
                    c=String.valueOf(dmem[pc]).charAt(0);
                    firstop = Integer.parseInt(String.valueOf(c));
                    reg[secondop]-=reg[firstop];
                   }
                   else if (literal==2)
                   {
                    String as = Integer.toString(dmem[pc]);
                    int Number = Integer.parseInt(as.substring(0, as.length()-1));
                    reg[dmem[pc]%10]-=Number;
                   }
                   pc+=1;
                   break;
                }
               case 5: // cmp
               {
                   firstop = dmem[pc]%100/10;
                   secondop = dmem[pc]%10;
                   if (reg[secondop]==reg[firstop])
                       cmem[pc]=51;// Литерал 1 - значения двух регистров равны
                   else
                       cmem[pc]=52;// Литерал 2 - значения двух регистров не равны
                   
                   pc+=1;
                   break;
               }                       
               case 6: // beq
               {
                   if (Integer.parseInt(Integer.toString(cmem[pc-1]).substring(1, 2))==1)
                       pc=dmem[pc];
                   else
                       pc+=1;
                   break;
               }
               case 7: // jmp
               {
                   pc = dmem[pc];
                   break;
               }
            }
   }
   
   public void runwhole(String text)
   {

       String[] codestrings = input(text);
       while (pc!=codestrings.length)
       {
           CmemFiller(codestrings[pc]);
           cmdexecutioner();
       }
    }
   public void runstepbystep(String text)
   {
       String txt = input_single(text);
       CmemFiller(txt);
       cmdexecutioner();
   }
   public void cleanregs()
   {
       pc=0;
       for (int i=0; i<reg.length; i++)
       {
           reg[i]=0;
       } 
   }
}
