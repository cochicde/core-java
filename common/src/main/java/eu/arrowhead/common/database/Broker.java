/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.database;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "broker", uniqueConstraints = {@UniqueConstraint(columnNames = {"address"})})
public class Broker {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotBlank
  @Size(min = 3, max = 255, message = "Address must be between 3 and 255 characters")
  private String address;

  @Min(value = 1, message = "Port can not be less than 1")
  @Max(value = 65535, message = "Port can not be greater than 65535")
  private Integer port;

  @Column(name = "is_secure")
  @Type(type = "yes_no")
  private Boolean secure = false;

  public Broker() {
  }

  public Broker(String address, Integer port, Boolean secure) {
    this.address = address;
    this.port = port;
    this.secure = secure;
  }

  public Long getId() {
    return id;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public Boolean isSecure() {
    return secure;
  }

  public void setSecure(Boolean secure) {
    this.secure = secure;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Broker)) {
      return false;
    }
    Broker broker = (Broker) o;
    return Objects.equals(address, broker.address) && Objects.equals(port, broker.port) && Objects.equals(secure, broker.secure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(address, port, secure);
  }

  @Override
  public String toString() {
    return "Broker{" + "id=" + id + ", address='" + address + '\'' + ", port=" + port + ", secure=" + secure + '}';
  }
}
