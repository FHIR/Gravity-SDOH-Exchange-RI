package org.hl7.gravity.refimpl.sdohexchange.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class DetailedEthnicityCode {

  public static final String SYSTEM = "urn:oid:2.16.840.1.113883.6.238";
  public static final Map<String, String> CODES;

  static {
    Map<String, String> codes = new HashMap<>();
    codes.put("2133-7", "Ethnicity");
    codes.put("2137-8", "Spaniard");
    codes.put("2148-5", "Mexican");
    codes.put("2155-0", "Central American");
    codes.put("2165-9", "South American");
    codes.put("2178-2", "Latin American");
    codes.put("2180-8", "Puerto Rican");
    codes.put("2182-4", "Cuban");
    codes.put("2184-0", "Dominican");
    codes.put("2138-6", "Andalusian");
    codes.put("2139-4", "Asturian");
    codes.put("2140-2", "Castillian");
    codes.put("2141-0", "Catalonian");
    codes.put("2142-8", "Belearic Islander");
    codes.put("2143-6", "Gallego");
    codes.put("2144-4", "Valencian");
    codes.put("2145-1", "Canarian");
    codes.put("2146-9", "Spanish Basque");
    codes.put("2149-3", "Mexican American");
    codes.put("2150-1", "Mexicano");
    codes.put("2151-9", "Chicano");
    codes.put("2152-7", "La Raza");
    codes.put("2153-5", "Mexican American Indian");
    codes.put("2156-8", "Costa Rican");
    codes.put("2157-6", "Guatemalan");
    codes.put("2158-4", "Honduran");
    codes.put("2159-2", "Nicaraguan");
    codes.put("2160-0", "Panamanian");
    codes.put("2161-8", "Salvadoran");
    codes.put("2162-6", "Central American Indian");
    codes.put("2163-4", "Canal Zone");
    codes.put("2166-7", "Argentinean");
    codes.put("2167-5", "Bolivian");
    codes.put("2168-3", "Chilean");
    codes.put("2169-1", "Colombian");
    codes.put("2170-9", "Ecuadorian");
    codes.put("2171-7", "Paraguayan");
    codes.put("2172-5", "Peruvian");
    codes.put("2173-3", "Uruguayan");
    codes.put("2174-1", "Venezuelan");
    codes.put("2175-8", "South American Indian");
    codes.put("2176-6", "Criollo");
    CODES = Collections.unmodifiableMap(codes);
  }

  public static Coding toCoding(String code) {
    String display = CODES.get(code);
    Assert.notNull("display", "Detailed Ethnicity code " + code + " not found.");
    return new Coding(SYSTEM, code, display);
  }
}
