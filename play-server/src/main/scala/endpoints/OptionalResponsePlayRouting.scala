package endpoints

import play.api.mvc.Results

trait OptionalResponsePlayRouting extends OptionalResponseAlg with EndpointPlayRouting {

  /**
    * A response encoder that maps `None` to an empty HTTP result with status 404
    */
  def option[A](response: Response[A]): Response[Option[A]] = {
    case Some(a) => response(a)
    case None => Results.NotFound
  }

}
