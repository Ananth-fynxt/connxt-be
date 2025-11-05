package connxt.psp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpApiResponse {

  @JsonProperty("query")
  private String query;

  @JsonProperty("status")
  private String status;

  @JsonProperty("continent")
  private String continent;

  @JsonProperty("continentCode")
  private String continentCode;

  @JsonProperty("country")
  private String country;

  @JsonProperty("countryCode")
  private String countryCode;

  @JsonProperty("region")
  private String region;

  @JsonProperty("regionName")
  private String regionName;

  @JsonProperty("city")
  private String city;

  @JsonProperty("district")
  private String district;

  @JsonProperty("zip")
  private String zip;

  @JsonProperty("lat")
  private Double latitude;

  @JsonProperty("lon")
  private Double longitude;

  @JsonProperty("timezone")
  private String timezone;

  @JsonProperty("offset")
  private Integer offset;

  @JsonProperty("currency")
  private String currency;

  @JsonProperty("isp")
  private String isp;

  @JsonProperty("org")
  private String organization;

  @JsonProperty("as")
  private String asn;

  @JsonProperty("asname")
  private String asnName;

  @JsonProperty("mobile")
  private Boolean mobile;

  @JsonProperty("proxy")
  private Boolean proxy;

  @JsonProperty("hosting")
  private Boolean hosting;
}
