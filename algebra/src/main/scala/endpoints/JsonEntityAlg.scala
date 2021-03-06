package endpoints

import scala.language.higherKinds

/**
  * Algebra interface for defining JSON entities in requests and responses.
  */
trait JsonEntityAlg extends EndpointAlg {

  /** Type class defining how to represent the `A` information as a JSON request entity */
  type JsonRequest[A]

  /** Defines a `RequestEntity[A]` given an implicit `JsonRequest[A]` */
  def jsonRequest[A : JsonRequest]: RequestEntity[A]


  /** Type class defining how to represent the `A` information as a JSON response entity */
  type JsonResponse[A]

  /** Defines a `Response[A]` given an implicit `JsonResponse[A]` */
  def jsonResponse[A : JsonResponse]: Response[A]

}
