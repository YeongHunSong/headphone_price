package hpPrice.common.naverCafe;

import lombok.Getter;

import java.util.List;

@Getter
public enum CategoryType {
    HEADPHONE(21, "[헤드폰]"),
    EARPHONE(102, "[이어폰]"),
    DAC_AMP(192, "[디바이스/앰프/DAC]"),
    CABLE(42, "[케이블/악세사리/기타]");

    private final int number;
    private final String name;

    CategoryType(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public static String getName(int number) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.getNumber() == number) {
                return categoryType.getName();
            }
        }
        throw new IllegalArgumentException("잘못된 카테고리 번호" + number);
    }

    public static boolean isCategory(int number) {
        for (CategoryType categoryType : CategoryType.values()) {
            if (categoryType.getNumber() == number) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getCategoryNumbers() {
        return List.of(21, 102, 192, 42);
    }
}
