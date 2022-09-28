package ltseed.cqucalendarsearchingtool;

public enum EVERY_CLASS_TIME {
    C1(1, 830, 915),
    C2(2, 925, 1010),
    C3(3, 1030, 1115),
    C4(4, 1125, 1210),
    C5(5, 1330, 1415),
    C6(6, 1425, 1510),
    C7(7, 1520, 1605),
    C8(8, 1625, 1710),
    C9(9, 1720, 1805),
    C10(10, 1900, 1945),
    C11(11, 1955, 2040),
    C12(12, 2050, 2135),
    C13(13, 2145, 2230);

    final int i, start, end;
    final TimePeriod tp;

    public static EVERY_CLASS_TIME getClassTime(int number){
        if(number<1||number>13) throw new IllegalArgumentException("错误地查询了非法的课程时间");
        switch (number){
            case 1 -> {
                return C1;
            }
            case 2 -> {
                return C2;
            }
            case 3 -> {
                return C3;
            }
            case 4 -> {
                return C4;
            }
            case 5 -> {
                return C5;
            }
            case 6 -> {
                return C6;
            }
            case 7 -> {
                return C7;
            }
            case 8 -> {
                return C8;
            }
            case 9 -> {
                return C9;
            }
            case 10 -> {
                return C10;
            }
            case 11 -> {
                return C11;
            }
            case 12 -> {
                return C12;
            }
            case 13 -> {
                return C13;
            }
            default -> throw new IllegalArgumentException();
        }
    }

    EVERY_CLASS_TIME(int i, int start, int end) {
        this.i = i;
        this.start = start;
        this.end = end;
        String sb = "0".repeat(Math.max(0, i - 1)) + '1';
        tp = new TimePeriod(sb);
    }

    @Override
    public String toString() {
        return start + " to " + end;
    }

    public boolean in(int time) {
        return time <= end && time >= start;
    }


    public ClassTime toClassTime(int week, int weekday) {
        return new ClassTime(week,i,weekday);
    }
}
