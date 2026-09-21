package com.ppfurtado.desafiovotacao.client;

public final class CpfValidatorUtil {

    private CpfValidatorUtil() {
    }

    public static String sanitize(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    public static boolean isValid(String cpf) {
        String cleanCpf = sanitize(cpf);

        if (cleanCpf.length() != 11) {
            return false;
        }

        // Verifica dígitos todos iguais
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += (cleanCpf.charAt(i) - '0') * peso--;
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            // Cálculo do 2º Dígito Verificador
            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += (cleanCpf.charAt(i) - '0') * peso--;
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            // Valida os dígitos calculados com os dígitos informados
            return (digito1 == (cleanCpf.charAt(9) - '0')) && (digito2 == (cleanCpf.charAt(10) - '0'));


        } catch (Exception e) {
            return false;
        }
    }
}
