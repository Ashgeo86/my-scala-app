package controllers
import javax.inject.Singleton
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {

  def index()= TODO
}
