package org.example;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;
import java.util.Arrays;

public class App {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("sv", "SE"));
        Scanner scanner = new Scanner(System.in);
        Timpris[] timpriser = new Timpris[24];

        while (true) {
            visaMeny();
            String val = scanner.nextLine();
            switch (val) {
                case "1":
                    timpriser = hämtaInmatning(scanner);
                    break;
                case "2":
                    beräknaMinMaxMedel(scanner, timpriser);
                    break;
                case "3":
                    sorteraPriser(scanner, timpriser);
                    break;
                case "4":
                    hittaBästaLaddningstid(scanner, timpriser);
                    break;
            }
            if (val.equalsIgnoreCase("e")) {
                break;
            }
        }
    }

    record Timpris(int pris, int timme) implements Comparable<Timpris> {
        @Override
        public int compareTo(Timpris annan) {
            return this.pris - annan.pris;
        }
    }

    public static String formattaMedKomma(double värde, int min) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(new Locale("sv", "SE"));
        df.setMinimumFractionDigits(min);
        df.setMaximumFractionDigits(2);
        return df.format(värde);
    }

    private static String formateraKlockslag(int timme) {
        return String.format("%02d", timme);
    }

    public static void visaMeny() {
        String meny = """
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                e. Avsluta
                """;
        System.out.print(meny);
    }

    private static Timpris[] hämtaInmatning(Scanner scanner) {
        Timpris[] timpriser = new Timpris[24];
        System.out.print("Inmatning\n");

        for (int i = 0; i < timpriser.length; i++) {
            System.out.print("Ange pris för timme " + formateraKlockslag(i) + " - " + formateraKlockslag(i + 1) + " i ören:\n");
            timpriser[i] = new Timpris(scanner.nextInt(), i);
            scanner.nextLine();
        }
        return timpriser;
    }

    public static void beräknaMinMaxMedel(Scanner scanner, Timpris[] timpriser) {
        System.out.print("Min, Max och Medel\n");

        Timpris min = timpriser[0];
        Timpris max = timpriser[0];
        int total = timpriser[0].pris;

        for (int i = 1; i < timpriser.length; i++) {
            if (timpriser[i].pris < min.pris) {
                min = timpriser[i];
            }
            if (timpriser[i].pris > max.pris) {
                max = timpriser[i];
            }
            total += timpriser[i].pris;
        }
        double medel = (double) total / timpriser.length;

        System.out.printf("Lägsta pris: %s-%s, %d öre/kWh\n", formateraKlockslag(min.timme), formateraKlockslag(min.timme + 1), min.pris);
        System.out.printf("Högsta pris: %s-%s, %d öre/kWh\n", formateraKlockslag(max.timme), formateraKlockslag(max.timme + 1), max.pris);
        System.out.printf("Medelpris: %s öre/kWh\n", formattaMedKomma(medel, 2));
    }

    public static void sorteraPriser(Scanner scanner, Timpris[] timpriser) {
        System.out.print("Sortera\n");

        Timpris[] kopia = Arrays.copyOf(timpriser, timpriser.length);
        Arrays.sort(kopia, (a, b) -> b.pris - a.pris);

        for (Timpris v : kopia) {
            System.out.printf("%s-%s %d öre\n", formateraKlockslag(v.timme), formateraKlockslag(v.timme + 1), v.pris);
        }
    }

    public static void hittaBästaLaddningstid(Scanner scanner, Timpris[] timpriser) {
        System.out.print("Bästa Laddningstid (4h)\n");

        int startTimma = Integer.MAX_VALUE;
        double lägstaMedel = Double.MAX_VALUE;

        for (int i = 0; i <= timpriser.length - 4; i++) {
            int summa = 0;

            for (int j = i; j < i + 4; j++) {
                summa += timpriser[j].pris;
            }
            double medel = summa / 4.0;
            if (medel < lägstaMedel) {
                lägstaMedel = medel;
                startTimma = i;
            }
        }
        System.out.printf("Påbörja laddning klockan %s\n", formateraKlockslag(startTimma));
        System.out.printf("Medelpris 4h: %s öre/kWh\n", formattaMedKomma(lägstaMedel, 0));
    }
}
